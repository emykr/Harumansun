package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object BlindessEvent {
    fun blindOthers(except: Player, durationSeconds: Int = 60) {
        val effect = PotionEffect(PotionEffectType.BLINDNESS, durationSeconds * 20, 1, false, false)
        Bukkit.getOnlinePlayers().filter { it != except }.forEach { it.addPotionEffect(effect) }
    }
}

