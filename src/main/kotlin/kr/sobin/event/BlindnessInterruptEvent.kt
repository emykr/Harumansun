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

    override fun apply(target: Player): Boolean {
        return try {
            val configDuration = plugin.config.getString("방해권.효과시간.blindness") ?: "60"
            val seconds = TimeUtil.parseTime(configDuration)
            val duration = seconds * 20  // 틱으로 변환 (1초 = 20틱)

            // 모든 온라인 플레이어에게 효과 적용 (사용자 제외)
            val effect = PotionEffect(
                PotionEffectType.BLINDNESS,
                duration,
                1
            )

            Bukkit.getOnlinePlayers()
                .filter { it != target }  // 사용자 제외
                .forEach { player ->
                    player.addPotionEffect(effect)
                    val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                        java.io.File(plugin.dataFolder, "messages.yml")
                    )
                    val msg = messages.getString("blindness_applied") ?: "${target.name}의 방해권으로 인해 실명 효과를 받았습니다."
                    player.sendMessage(msg)
                }

            plugin.logger.info("[BlindnessEvent] ${target.name}의 방해권으로 인해 모든 플레이어가 ${seconds}초간 실명 효과를 받았습니다.")
            true
        } catch (e: Exception) {
            plugin.logger.warning("[BlindnessEvent] 효과 적용 중 오류 발생: ${e.message}")
            false
        }
    }
}
