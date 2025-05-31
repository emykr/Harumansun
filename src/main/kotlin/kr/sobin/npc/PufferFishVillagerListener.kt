package kr.sobin.npc

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

class PufferFishVillagerListener(private val plugin: JavaPlugin) : Listener {
    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        if (event.npc.name == PufferFishVillager.getNPCName(plugin)) {
            PufferFishVillager.openPufferFishShop(event.clicker, plugin)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.view.title != PufferFishVillager.getShopTitle(plugin)) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return
        if (event.slot != PufferFishVillager.SLOT_PUFFERFISH) return

        handlePufferFishPurchase(player)
    }

    private fun handlePufferFishPurchase(player: Player) {
        val configPrice = plugin.config.getString("복어 상점.가격") ?: "100"
        val price = configPrice.toDoubleOrNull() ?: 100.0

        if (economy == null) {
            player.sendMessage(Component.text("[상점] 경제 플러그인(Vault)이 감지되지 않았습니다.")
                .color(NamedTextColor.RED))
            return
        }

        if (economy!!.getBalance(player) < price) {
            player.sendMessage(Component.text("[상점] 돈이 부족합니다. (필요: $price)")
                .color(NamedTextColor.RED))
            PufferFishVillager.playBuySound(player, false)
            return
        }

        economy!!.withdrawPlayer(player, price)

        // 일반 복어 지급
        val pufferfish = org.bukkit.inventory.ItemStack(Material.PUFFERFISH)
        player.inventory.addItem(pufferfish)

        player.sendMessage(Component.text("[상점] 복어를 구매했습니다! (-$price)")
            .color(NamedTextColor.GREEN))
        player.closeInventory()
        PufferFishVillager.playBuySound(player, true)
    }
}
