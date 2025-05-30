package kr.sobin.event

import org.bukkit.World
import org.bukkit.entity.Player

object RandomLightningVoltEvent {
    fun strikeLightning(target: Player) {
        val world: World = target.world
        world.strikeLightningEffect(target.location)
    }
}

