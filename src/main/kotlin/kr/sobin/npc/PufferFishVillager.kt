package kr.sobin.npc

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import dev.lone.itemsadder.api.FontImages.FontImageWrapper
import kr.sobin.core.ShopCore

class PufferFishVillager {
    companion object {
        const val SLOT_PUFFERFISH = 22  // 더블 체스트의 중앙

        fun createPufferFish(plugin: JavaPlugin): ItemStack {
            val pufferfish = ItemStack(Material.PUFFERFISH)
            val meta = pufferfish.itemMeta
            val itemName = plugin.config.getString("복어 상점.아이템.이름") ?: "&e복어"
            val itemDescription = plugin.config.getString("복어 상점.아이템.설명")
                ?.replace("%price%", plugin.config.getString("복어 상점.가격") ?: "100")

            meta?.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', itemName))
            if (!itemDescription.isNullOrEmpty()) {
                meta?.lore = listOf(org.bukkit.ChatColor.translateAlternateColorCodes('&', itemDescription))
            }
            pufferfish.itemMeta = meta
            return pufferfish
        }

        fun openPufferFishShop(player: Player, plugin: JavaPlugin) {
            val title = getShopTitle(plugin)
            val inv: Inventory = Bukkit.createInventory(null, 54, title)

            // config에서 배경 설정 확인
            if (plugin.config.getBoolean("복어 상점.배경", true)) {
                // 배경 유리판 설정
                val purplePane = ShopCore.createPane(Material.PURPLE_STAINED_GLASS_PANE)
                val yellowPane = ShopCore.createPane(Material.YELLOW_STAINED_GLASS_PANE)

                // 더블 체스트에 맞게 장식 슬롯 위치 설정
                val purpleSlots = listOf(
                    0, 1, 2, 9, 10, 11, 18, 19, 20,  // 왼쪽 위
                    6, 7, 8, 15, 16, 17, 24, 25, 26,  // 오른쪽 위
                    27, 28, 29, 36, 37, 38, 45, 46, 47,  // 왼쪽 아래
                    33, 34, 35, 42, 43, 44, 51, 52, 53   // 오른쪽 아래
                )
                val yellowSlots = listOf(
                    3, 4, 5, 12, 14, 21, 22, 23,  // 위쪽
                    30, 31, 32, 39, 41, 48, 49, 50  // 아래쪽
                )

                // 배경 설정
                purpleSlots.forEach { slot -> inv.setItem(slot, purplePane) }
                yellowSlots.forEach { slot -> inv.setItem(slot, yellowPane) }
            }

            // 복어 아이템 배치
            inv.setItem(SLOT_PUFFERFISH, createPufferFish(plugin))
            player.openInventory(inv)
        }

        fun getShopTitle(plugin: JavaPlugin): String {
            val rawTitle = plugin.config.getString("복어 상점.제목") ?: "&e복어 상점"
            return org.bukkit.ChatColor.translateAlternateColorCodes('&',
                FontImageWrapper.replaceFontImages(rawTitle))
        }

        fun getNPCName(plugin: JavaPlugin): String {
            return plugin.config.getString("복어 상점.이름") ?: "복어 상점"
        }

        fun playBuySound(player: Player, success: Boolean) {
            ShopCore.playBuySound(player, success)
        }
    }
}
