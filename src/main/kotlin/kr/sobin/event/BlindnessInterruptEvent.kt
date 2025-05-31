package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.plugin.java.JavaPlugin
import kr.sobin.core.TimeUtil

class BlindnessInterruptEvent(plugin: JavaPlugin) : AbstractInterruptEvent(plugin) {
    override val displayName = "실명"
    override val configKey = "blindness"

    // 마지막 방해권 사용자를 저장하는 프로퍼티 추가
    var lastUser: Player? = null

    override fun apply(target: Player): Boolean {
        return try {
            lastUser = target
            applyBlindnessInterrupt(target, plugin)
            true
        } catch (e: Exception) {
            plugin.logger.warning("[BlindnessEvent] 효과 적용 중 오류 발생: ${e.message}")
            false
        }
    }

    private fun applyBlindnessInterrupt(player: Player, plugin: JavaPlugin) {
        val target = findTargetPlayer(player, 5.0) ?: player

        val configDuration = plugin.config.getString("방해권.효과시간.blindness") ?: "60"
        val seconds = TimeUtil.parseTime(configDuration)
        val duration = seconds * 20  // 틱으로 변환 (1초 = 20틱)

        val effect = PotionEffect(PotionEffectType.BLINDNESS, duration, 1)

        // 방해권 사용자를 제외한 모든 온라인 플레이어에게 블라인드 효과 적용
        for (online in Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.addPotionEffect(effect)
            }
        }

        val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
            java.io.File(plugin.dataFolder, "messages.yml")
        )
        val msg = messages.getString("blindness_applied")?.replace("{player}", player.name)
            ?: "${player.name}의 방해권으로 인해 실명 효과를 받았습니다."

        // 방해권 사용자를 제외한 모든 플레이어에게 메시지 전송
        for (online in Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.sendMessage(msg)
            }
        }

        plugin.logger.info("[BlindnessEvent] ${player.name}의 방해권으로 인해 ${target.name} 및 전체 플레이어(본인 제외)가 ${seconds}초간 실명 효과를 받았습니다.")
    }

    private fun findTargetPlayer(player: Player, range: Double): Player? {
        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction

        return player.getNearbyEntities(range, range, range)
            .filterIsInstance<Player>()
            .filter { it != player }
            .map { entity ->
                val angle: Double = direction.angle(
                    entity.eyeLocation.toVector().subtract(eyeLocation.toVector())
                ).toDouble()
                Pair(entity, angle)
            }
            .filter { it.second <= Math.toRadians(30.0) }
            .minByOrNull { it.second }
            ?.first
    }
}
