package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

object RandomLightningVoltEvent {
    fun strikeLightning(player: Player) {
        player.world.strikeLightning(player.location)
        Bukkit.getLogger().info("[LightningEvent] ${player.name}에게 번개가 떨어졌습니다!")
    }
}

