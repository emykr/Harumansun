package kr.sobin.npc

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import dev.lone.itemsadder.api.FontImages.FontImageWrapper
import kr.sobin.core.ShopCore

class InterrupterVillager {
    companion object {
        const val SLOT_PAPER = 22  // 더블 체스트의 중앙

        fun createInterruptTicket(plugin: JavaPlugin): ItemStack {
            Bukkit.getLogger().info("[DEBUG] createInterruptTicket 호출됨")
            val paper = ItemStack(Material.PAPER)
            val meta = paper.itemMeta
            val itemName = plugin.config.getString("방해권.이름") ?: "&l&5랜덤 방해권"
            val itemDescription = plugin.config.getString("방해권.설명") ?: ""

            meta?.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', itemName))

            if (itemDescription.isNotBlank()) {
                meta?.lore = itemDescription
                    .split("\n")  // 명시적으로 \n으로 분리
                    .map { org.bukkit.ChatColor.translateAlternateColorCodes('&', it) }
            }

            paper.itemMeta = meta
            return paper
        }

        fun openInterrupterShop(player: Player, plugin: JavaPlugin) {
            Bukkit.getLogger().info("[DEBUG] openInterrupterShop 호출됨: player=${player.name}")
            val title = getShopTitle(plugin)
            Bukkit.getLogger().info("[DEBUG] 방해권 상점 제목='${title}'")
            val inv: Inventory = Bukkit.createInventory(null, 54, title)

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

            // 방해권 아이템 배치
            inv.setItem(SLOT_PAPER, createInterruptTicket(plugin))
            player.openInventory(inv)
            Bukkit.getLogger().info("[DEBUG] 방해권 상점 인벤토리 openInventory 호출 완료")
        }

        fun getShopTitle(plugin: JavaPlugin): String {
            val rawTitle = plugin.config.getString("방해권 상인.제목") ?: "&e방해권 상인"
            Bukkit.getLogger().info("[DEBUG] getInterrupterShopTitle: rawTitle='${rawTitle}'")
            return org.bukkit.ChatColor.translateAlternateColorCodes('&',
                FontImageWrapper.replaceFontImages(rawTitle))
        }

        fun getNPCName(plugin: JavaPlugin): String {
            val name = plugin.config.getString("방해권 상인.이름") ?: "방해권 상인"
            Bukkit.getLogger().info("[DEBUG] getInterrupterNPCName: name='${name}'")
            return name
        }

        fun playBuySound(player: Player, success: Boolean) {
            ShopCore.playBuySound(player, success)
        }
    }
}

