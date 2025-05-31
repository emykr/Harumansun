package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object BlindessEvent {
    fun blindOthers(player: Player) {
        // 본인을 제외한 모든 온라인 플레이어에게 실명 효과 적용
        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { target ->
                target.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1))
            }
    }
}

