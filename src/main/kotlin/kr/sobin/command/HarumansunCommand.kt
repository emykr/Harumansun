package kr.sobin.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class HarumansunCommand(private val plugin: JavaPlugin) : CommandExecutor, TabCompleter {
    companion object {
        // FakePlayer 관련 코드 제거
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // player 명령어 관련 코드 제거
        // ...기존 harumansun 명령어...
        if (args.isNotEmpty() && args[0].equals("reload", true)) {
            plugin.reloadConfig()
            sender.sendMessage("[Harumansun] 설정이 리로드되었습니다.")
            return true
        }
        sender.sendMessage("[Harumansun] 사용법: /harumansun reload")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        // player 명령어 관련 자동완성 제거
        if (command.name.equals("harumansun", true)) {
            if (args.size == 1) {
                return listOf("reload").filter { it.startsWith(args[0], ignoreCase = true) }
            }
        }
        return emptyList()
    }
}
