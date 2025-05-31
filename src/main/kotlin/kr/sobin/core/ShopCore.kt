package kr.sobin.core

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ShopCore {
    fun createPane(material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta?.setDisplayName(" ")
        item.itemMeta = meta
        return item
    }

    fun playBuySound(player: Player, success: Boolean) {
        val sound = if (success) "entity.experience_orb.pickup" else "entity.villager.no"
        player.playSound(player.location, sound, 1.0f, 1.0f)
    }
}

