package kr.sobin.npc

import kr.sobin.npc.FishTraderGUI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player

class PufferFishVillagerListener(private val plugin: org.bukkit.plugin.java.JavaPlugin) : Listener {
    private val shopTitle = "복어 상점"
    private val slotPufferfish = 13
    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        val npc = event.npc
        val player = event.clicker
        // "복어 상점" 이름의 NPC만 처리
        if (npc.name == shopTitle) {
            PufferFishVillager.openPufferFishShop(player, plugin)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val viewTitle = event.view.title
        if (viewTitle != shopTitle) return
        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return
        event.isCancelled = true
        if (event.slot == slotPufferfish && event.currentItem != null && event.currentItem?.type == org.bukkit.Material.PUFFERFISH) {
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
