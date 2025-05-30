package kr.sobin.npc

import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class PufferFishVillager(private val plugin: JavaPlugin) : Listener {
    companion object {
        const val SHOP_TITLE = "복어 상점"
        const val SLOT_PUFFERFISH = 13

        fun openPufferFishShop(player: Player, plugin: JavaPlugin) {
            val inv = org.bukkit.Bukkit.createInventory(null, 27, SHOP_TITLE)
            val item = org.bukkit.inventory.ItemStack(org.bukkit.Material.PUFFERFISH)
            val meta = item.itemMeta
            meta?.setDisplayName("§e복어")
            val configPrice = plugin.config.getString("복어 상점.가격") ?: "100"
            val price = configPrice.toDoubleOrNull() ?: 100.0
            meta?.lore = listOf("§b현재 복어 시세: ${price.toInt()}원")
            item.itemMeta = meta
            inv.setItem(SLOT_PUFFERFISH, item)
            player.openInventory(inv)
        }
    }
    private val shopTitle = SHOP_TITLE
    private val slotpufferfish = SLOT_PUFFERFISH

    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val viewTitle = event.view.title
        if (viewTitle != shopTitle) return

        // 클릭한 인벤토리가 상점 인벤토리(Top)인지 확인
        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return

        // 모든 클릭 막기 (기본)
        event.isCancelled = true

        // 복어 슬롯 클릭 시에만 구매 처리
        if (event.slot == slotpufferfish && event.currentItem != null && event.currentItem?.type == org.bukkit.Material.PUFFERFISH) {
            val configPrice = plugin.config.getString("복어 상점.가격") ?: "100"
            val price = configPrice.toDoubleOrNull() ?: 100.0
            val eco = economy
            if (eco == null) {
                player.sendMessage("[상점] 경제 시스템을 찾을 수 없습니다.")
                return
            }
            if (eco.getBalance(player) < price) {
                player.sendMessage("[상점] 돈이 부족합니다. (필요: ${price.toInt()}원)")
                return
            }
            // 돈 차감 및 아이템 지급
            eco.withdrawPlayer(player, price)
            val pufferfish = org.bukkit.inventory.ItemStack(org.bukkit.Material.PUFFERFISH)
            val meta = pufferfish.itemMeta
            meta?.setDisplayName("§e복어")
            meta?.lore = listOf("§b구매가: ${price.toInt()}원")
            pufferfish.itemMeta = meta
            player.inventory.addItem(pufferfish)
            player.sendMessage("[상점] 복어를 ${price.toInt()}원에 구매했습니다!")
            player.closeInventory()
        }
    }
}
