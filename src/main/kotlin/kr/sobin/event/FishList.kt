package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.random.Random

object FishList {
    val normalFishKeys = listOf(
        "anthias", "blazorb_jellyfish", "blue_eel", "blue_grenadier", "bream", "bubblefish",
        "chromis", "coralfish", "crimson_bubblefish", "gemfish", "green_barb", "greenfish",
        "icefish", "jellyfish", "lava_anthias", "lava_eel", "magma_trout", "molten_coralfish",
        "obsidianfish", "rosy_barb", "shrimp", "slimefish", "stingray", "sunfish", "trout",
        "tuna", "warped_shrimp"
    )
    val fishMaterials = listOf(Material.COD, Material.PUFFERFISH)
}