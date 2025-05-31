package kr.sobin.npc

import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.inventory.ItemStack
import org.bukkit.util.RayTraceResult
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.random.Random
import kr.sobin.event.BlindessEvent
import kr.sobin.event.RandomJailEvent
import kr.sobin.event.RandomLightningVoltEvent
import kr.sobin.event.RandomFishEvent

class InterrupterVillagerListener(private val plugin: JavaPlugin) : Listener {
    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        if (event.npc.name == InterrupterVillager.getNPCName(plugin)) {
            InterrupterVillager.openInterrupterShop(event.clicker, plugin)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.view.title != InterrupterVillager.getShopTitle(plugin)) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        if (clickedInv != event.view.topInventory) return
        if (event.slot != InterrupterVillager.SLOT_PAPER) return

        handleTicketPurchase(player)
    }

    private fun handleTicketPurchase(player: Player) {
        val configPrice = plugin.config.getString("방해권 상인.가격") ?: "100"
        val price = configPrice.toDoubleOrNull() ?: 100.0

        if (economy == null) {
            player.sendMessage(Component.text("[상점] 경제 플러그인(Vault)이 감지되지 않았습니다.")
                .color(NamedTextColor.RED))
            return
        }

        if (economy!!.getBalance(player) < price) {
            player.sendMessage(Component.text("[상점] 돈이 부족합니다. (필요: $price)")
                .color(NamedTextColor.RED))
            InterrupterVillager.playBuySound(player, false)
            return
        }

        economy!!.withdrawPlayer(player, price)
        player.sendMessage(Component.text("[상점] 랜덤 방해권을 구매했습니다! (-$price)")
            .color(NamedTextColor.GREEN))
        player.inventory.addItem(InterrupterVillager.createInterruptTicket(plugin))
        player.closeInventory()
        InterrupterVillager.playBuySound(player, true)
    }

    @EventHandler
    fun onPlayerUseTicket(event: PlayerInteractEntityEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        if (!isValidInterruptTicket(item)) return

        val target = (event.rightClicked as? Player) ?: player
        val result = applyRandomEffect(target)

        consumeTicket(item, player)
        event.isCancelled = true
        sendEffectMessage(player, target, result)
    }

    private fun isValidInterruptTicket(item: ItemStack): Boolean {
        if (item.type != Material.PAPER) return false

        val meta = item.itemMeta ?: return false
        if (!meta.hasDisplayName()) return false

        val configItemName = plugin.config.getString("방해권.이름") ?: "&l&5랜덤 방해권"
        val expectedDisplayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', configItemName)

        return meta.displayName == expectedDisplayName
    }

    private fun applyRandomEffect(target: Player): String {
        val chanceBlind = plugin.config.getInt("방해권.확률.blindness", 40)
        val chanceJail = plugin.config.getInt("방해권.확률.jail", 30)
        val chanceLightning = plugin.config.getInt("방해권.확률.lightning", 20)
        val chanceFish = plugin.config.getInt("방해권.확률.fish", 10)

        val totalWeight = chanceBlind + chanceJail + chanceLightning + chanceFish
        val rand = Random.nextInt(totalWeight)

        return when {
            rand < chanceBlind -> {
                BlindessEvent.blindOthers(target)
                "실명"
            }
            rand < chanceBlind + chanceJail -> {
                RandomJailEvent.jailPlayer(target)
                "감옥"
            }
            rand < chanceBlind + chanceJail + chanceLightning -> {
                RandomLightningVoltEvent.strikeLightning(target)
                "번개"
            }
            else -> {
                RandomFishEvent.releaseRandomFish(target)
                "물고기 방생"
            }
        }
    }

    private fun consumeTicket(item: ItemStack, player: Player) {
        if (item.amount > 1) {
            item.amount -= 1
        } else {
            player.inventory.removeItem(item)
        }
    }

    private fun sendEffectMessage(player: Player, target: Player, effect: String) {
        player.sendMessage(Component.text()
            .content("[알림] ")
            .color(NamedTextColor.YELLOW)
            .append(Component.text()
                .content("${target.name}에게 ")
                .color(NamedTextColor.WHITE)
                .append(Component.text()
                    .content(effect)
                    .color(NamedTextColor.RED)
                    .append(Component.text()
                        .content(" 효과를 걸었습니다!")
                        .color(NamedTextColor.WHITE)
                        .build())
                    .build())
                .build())
            .build())
    }
}
