package kr.sobin.event

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class LightningInterruptEvent(plugin: JavaPlugin) : AbstractInterruptEvent(plugin) {
    override val displayName = "번개"
    override val configKey = "lightning"

    override fun apply(target: Player): Boolean {
        return try {
            target.world.strikeLightning(target.location)
            plugin.logger.info("[LightningEvent] ${target.name}에게 번개가 떨어졌습니다!")
            val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                java.io.File(plugin.dataFolder, "messages.yml")
            )
            val msg = messages.getString("lightning_strike") ?: "번개가 떨어졌습니다!"
            target.sendMessage(msg)
            true
        } catch (e: Exception) {
            plugin.logger.warning("[LightningEvent] 효과 적용 중 오류 발생: ${e.message}")
            false
        }
    }
}
