package kr.sobin.command

import kr.sobin.fakeplayer.NMSFakePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayerCommand(private val plugin: JavaPlugin) : CommandExecutor, TabCompleter {
    companion object {
        private val fakePlayers = mutableMapOf<String, NMSFakePlayer>()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("/player <summon|remove|inventory|select|settings>")
            return true
        }

        when (args[0].lowercase()) {
            "summon" -> {
                if (args.size < 2) {
                    sender.sendMessage("/player summon <닉네임> <스킨(UUID/URL, 생략가능)>")
                    return true
                }
                if (sender !is Player) {
                    sender.sendMessage("플레이어만 사용할 수 있습니다.")
                    return true
                }

                val name = args[1]
                val skin = args.getOrNull(2) // 스킨은 추후 구현

                if (fakePlayers.containsKey(name)) {
                    sender.sendMessage("이미 해당 이름의 FakePlayer가 존재합니다.")
                    return true
                }

                try {
                    val fake = NMSFakePlayer(name, sender.location)
                    fake.spawn(sender)
                    fakePlayers[name] = fake
                    sender.sendMessage("FakePlayer $name 소환 완료!")
                } catch (e: Exception) {
                    sender.sendMessage("§c[FakePlayer] Failed to spawn: ${e.message}")
                    e.printStackTrace()
                }
                return true
            }

            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage("/player remove <닉네임>")
                    return true
                }
                val name = args[1]
                val fake = fakePlayers[name]
                if (fake == null) {
                    sender.sendMessage("해당 이름의 FakePlayer가 없습니다.")
                    return true
                }

                if (sender is Player) {
                    try {
                        fake.destroy(sender)
                        fakePlayers.remove(name)
                        sender.sendMessage("FakePlayer $name 제거 완료!")
                    } catch (e: Exception) {
                        sender.sendMessage("§c[FakePlayer] Failed to remove: ${e.message}")
                        e.printStackTrace()
                    }
                }
                return true
            }

            "skin" -> {
                if (args.size < 3) {
                    sender.sendMessage("/player skin <닉네임> <스킨닉네임|스킨UUID|base64>")
                    return true
                }
                val name = args[1]
                val skin = args[2]
                val fake = fakePlayers[name]
                if (fake == null) {
                    sender.sendMessage("해당 이름의 FakePlayer가 없습니다.")
                    return true
                }
                // 새 스킨으로 FakePlayer 재생성 및 교체
                if (sender is Player) {
                    try {
                        fake.destroy(sender)
                        val newFake = kr.sobin.fakeplayer.NMSFakePlayer(name, sender.location, skin)
                        newFake.spawn(sender)
                        fakePlayers[name] = newFake
                        sender.sendMessage("FakePlayer $name 의 스킨이 변경되었습니다!")
                    } catch (e: Exception) {
                        sender.sendMessage("§c[FakePlayer] Failed to change skin: ${e.message}")
                        e.printStackTrace()
                    }
                }
                return true
            }
            // ...추가 명령어 구현 예정...
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (args.size == 1) {
            return listOf("summon", "remove", "inventory", "select", "settings")
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        if (args.size == 2) {
            return when (args[0].lowercase()) {
                "remove", "inventory" -> fakePlayers.keys
                    .filter { it.startsWith(args[1], ignoreCase = true) }
                else -> null
            }
        }
        return emptyList()
    }
}

