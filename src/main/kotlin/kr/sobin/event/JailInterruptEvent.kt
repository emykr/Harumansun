package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class JailInterruptEvent(plugin: JavaPlugin) : AbstractInterruptEvent(plugin) {
    override val displayName = "감옥"
    override val configKey = "jail"

    override fun apply(target: Player): Boolean {
        return try {
            // /jail <닉네임> 감옥 3m 명령 실행
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jail ${target.name} 감옥 3m")
            plugin.logger.info("[JailEvent] ${target.name}가 감옥에 갇혔습니다!")
            val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                java.io.File(plugin.dataFolder, "messages.yml")
            )
            val msg = messages.getString("jailed") ?: "감옥에 갇혔습니다!"
            target.sendMessage(msg)
            true
        } catch (e: Exception) {
            plugin.logger.warning("[JailEvent] 효과 적용 중 오류 발생: ${e.message}")
            false
        }
    }
}
