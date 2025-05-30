package kr.sobin.npc

import kr.sobin.material.Aquaculture
import kr.sobin.material.CompatUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class FishTraderGUI {
    companion object {
        fun open(player: Player) {
            val doubleChest: Inventory = Bukkit.createInventory(null, 54, "물고기 상인")

            // 0~8, 45~53: 빨간/노란색 유리판
            val redPane = CompatUtil.createVanillaItem(Material.RED_STAINED_GLASS_PANE)
            val yellowPane = CompatUtil.createVanillaItem(Material.YELLOW_STAINED_GLASS_PANE)
            for (i in 0..8) {
                doubleChest.setItem(i, if (i % 2 == 0) redPane else yellowPane)
            }
            for (i in 45..53) {
                doubleChest.setItem(i, if (i % 2 == 0) redPane else yellowPane)
            }

            // Material enum 순서대로 아이템 배치 (9~44)
            val materials = Aquaculture.Material.entries.toTypedArray()
            for ((index, mat) in materials.withIndex()) {
                val slot = 9 + index
                if (slot > 44) break
                val item = when (slot) {
                    38 -> CompatUtil.createVanillaItem(Material.COD)
                    39 -> CompatUtil.createVanillaItem(Material.SALMON)
                    40 -> CompatUtil.createVanillaItem(Material.TROPICAL_FISH)
                    44 -> CompatUtil.createVanillaItem(Material.PUFFERFISH)
                    else -> createFishItemCompat(mat)
                }
                doubleChest.setItem(slot, item)
            }

            player.openInventory(doubleChest)
        }

        private fun createFishItemCompat(mat: Aquaculture.Material): ItemStack {
            return if (CompatUtil.isModdedServer()) {
                // enum에 저장된 id를 사용해 모드 아이템 생성
                val (modid, itemName) = mat.id.split(":", limit = 2).let {
                    if (it.size == 2) it[0] to it[1] else "aquaculture" to mat.name.lowercase()
                }
                CompatUtil.createModItem(modid, itemName) ?: customFishItem(mat)
            } else {
                customFishItem(mat)
            }
        }

        private fun customFishItem(mat: Aquaculture.Material): ItemStack {
            val item = ItemStack(Material.PAPER)
            val meta: ItemMeta = item.itemMeta!!
            meta.setDisplayName("§b${mat.name}")
            item.itemMeta = meta
            return item
        }
    }
}


