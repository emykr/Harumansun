package kr.sobin.fakeplayer

import java.util.UUID
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class AbstractFakePlayer(
    val name: String,
    val uuid: UUID = UUID.randomUUID(),
    var location: Location
) : IFakePlayer {
    abstract fun spawn(to: Player)
    abstract fun destroy(to: Player)
}

