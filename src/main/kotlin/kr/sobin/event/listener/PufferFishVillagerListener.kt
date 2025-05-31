package kr.sobin.event.listener

import kr.sobin.npc.PufferFishVillager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemStack

class PufferFishVillagerListener(private val plugin: JavaPlugin) : Listener {
    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        if (event.npc.name == PufferFishVillager.Companion.getNPCName(plugin)) {
            PufferFishVillager.Companion.openPufferFishShop(event.clicker, plugin)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.view.title != PufferFishVillager.Companion.getShopTitle(plugin)) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return
        if (event.slot != PufferFishVillager.Companion.SLOT_PUFFERFISH) return

        handlePufferFishPurchase(player)
    }

    private fun handlePufferFishPurchase(player: Player) {
        val configPrice = plugin.config.getString("복어 상점.가격") ?: "100"
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
            val msg = messages.getString("pufferfish_shop_purchase_fail") ?: "복어를 구매하지 못했습니다. 잔액이 부족합니다."
            player.sendMessage(msg.replace("{price}", price.toString()))
            PufferFishVillager.Companion.playBuySound(player, false)
            return
        }

        economy!!.withdrawPlayer(player, price)

        // 일반 복어 지급
        val pufferfish = ItemStack(Material.PUFFERFISH)
        player.inventory.addItem(pufferfish)

        val itemName = plugin.config.getString("복어 상점.아이템.이름") ?: "복어"
        val msg = messages.getString("pufferfish_shop_purchase_success") ?: "복어를 구매했습니다! {item}을(를) 받았습니다."
        player.sendMessage(msg.replace("{item}", itemName).replace("{price}", price.toString()))
        player.closeInventory()
        PufferFishVillager.Companion.playBuySound(player, true)
    }
}
