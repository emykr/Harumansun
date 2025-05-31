package kr.sobin.event

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class AbstractInterruptEvent(protected val plugin: JavaPlugin) {
    /**
     * 이벤트 발동 시 표시될 이름
     */
    abstract val displayName: String

    /**
     * 이벤트가 발동될 확률 (config에서 읽어올 키 값)
     */
    abstract val configKey: String

    /**
     * 이벤트 효과를 적용
     * @param target 효과를 받을 대상 플레이어
     * @return 이벤트 적용 성공 여부
     */
    abstract fun apply(target: Player): Boolean

    /**
     * config에서 이벤트 확률을 읽어옴
     */
    fun getChance(): Int {
        return plugin.config.getInt("방해권.확률.$configKey", 0)
    }
}
