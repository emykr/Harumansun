package kr.sobin.event.listener

import kr.sobin.npc.InterrupterVillager
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class InterrupterVillagerListener(private val plugin: JavaPlugin) : Listener {
    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        if (event.npc.name == InterrupterVillager.Companion.getNPCName(plugin)) {
            InterrupterVillager.Companion.openInterrupterShop(event.clicker, plugin)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.view.title != InterrupterVillager.Companion.getShopTitle(plugin)) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return
        if (event.slot != InterrupterVillager.Companion.SLOT_PAPER) return

        handleTicketPurchase(player)
    }

    private fun handleTicketPurchase(player: Player) {
        val configPrice = plugin.config.getString("방해권 상인.가격") ?: "100"
        val price = configPrice.toDoubleOrNull() ?: 100.0

        val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
            java.io.File(plugin.dataFolder, "messages.yml")
        )

        if (economy == null) {
            val msg = messages.getString("shop_no_economy") ?: "[상점] 경제 플러그인(Vault)이 감지되지 않았습니다."
            player.sendMessage(msg)
            return
        }

        if (economy!!.getBalance(player) < price) {
            val msg = messages.getString("shop_purchase_fail") ?: "구매에 실패했습니다. 잔액이 부족합니다."
            player.sendMessage(msg.replace("{price}", price.toString()))
            InterrupterVillager.Companion.playBuySound(player, false)
            return
        }

        economy!!.withdrawPlayer(player, price)
        val itemName = plugin.config.getString("방해권.이름") ?: "랜덤 방해권"
        val msg = messages.getString("shop_purchase_success") ?: "구매가 완료되었습니다! {item}을(를) 획득했습니다."
        player.sendMessage(msg.replace("{item}", itemName).replace("{price}", price.toString()))
        player.inventory.addItem(InterrupterVillager.Companion.createInterruptTicket(plugin))
        player.closeInventory()
        InterrupterVillager.Companion.playBuySound(player, true)
    }
}
