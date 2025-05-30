package kr.sobin.npc

import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.block.Action
import org.bukkit.plugin.java.JavaPlugin
import kr.sobin.event.BlindessEvent
import kr.sobin.event.RandomJailEvent
import kr.sobin.event.RandomLightningVoltEvent
import kotlin.random.Random

@Suppress("DEPRECATION")
class InterrupterVillagerListener(private val plugin: JavaPlugin) : Listener {
    private val shopTitle = "방해권 상인"
    private val slotPaper = 13

    private val economy: Economy? by lazy {
        plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inv = event.inventory
        val player = event.whoClicked as? Player ?: return
        val viewTitle = event.view.title
        if (viewTitle != shopTitle) return

        if (event.slot != slotPaper) {
            event.isCancelled = true
            return
        }

        val configPrice = plugin.config.getString("방해권 상인.가격") ?: "100"
        val price = configPrice.toDoubleOrNull() ?: 100.0
        val eco = economy
        if (eco == null) {
            player.sendMessage("[상점] 경제 플러그인(Vault)이 감지되지 않았습니다.")
            event.isCancelled = true
            return
        }
        if (eco.getBalance(player) < price) {
            player.sendMessage("[상점] 돈이 부족합니다. (필요: $price)")
            event.isCancelled = true
            return
        }
        eco.withdrawPlayer(player, price)
        player.sendMessage("[상점] 랜덤 방해권을 구매했습니다! (-$price)")
        val item = inv.getItem(slotPaper)
        if (item != null) player.inventory.addItem(item.clone())
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        if (item.type != org.bukkit.Material.PAPER) return
        val meta = item.itemMeta ?: return
        val displayName = if (meta.hasDisplayName()) meta.displayName else null
        val legacyName = meta.displayName
        val isTicket = (displayName?.contains("랜덤 방해권") == true) || (legacyName?.contains("랜덤 방해권") == true)
        if (!isTicket) return

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        // 타겟이 없으면 효과 발동 X
        val target = findTargetPlayer(player, 5.0)
        if (target == null) {
            player.sendMessage("[방해권] 타겟팅된 플레이어가 없습니다.")
            return
        }

        // 콘피그에서 확률 읽기
        val chanceBlind = plugin.config.getInt("방해권.확률.blindness", 40)
        val chanceJail = plugin.config.getInt("방해권.확률.jail", 30)
        val chanceLightning = plugin.config.getInt("방해권.확률.lightning", 30)
        val total = chanceBlind + chanceJail + chanceLightning

        val rand = Random.nextInt(total)
        when {
            rand < chanceBlind -> BlindessEvent.blindOthers(target)
            rand < chanceBlind + chanceJail -> RandomJailEvent.jailPlayer(target)
            else -> RandomLightningVoltEvent.strikeLightning(target)
        }
        player.sendMessage("[방해권] ${target.name}에게 랜덤 방해 효과가 적용되었습니다!")

        // 방해권 1장 소모
        if (item.amount > 1) {
            item.amount = item.amount - 1
        } else {
            player.inventory.removeItem(item)
        }
        event.isCancelled = true
    }

    private fun findTargetPlayer(player: Player, range: Double): Player? {
        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction
        val nearbyEntities = player.getNearbyEntities(range, range, range)
        for (entity in nearbyEntities) {
            if (entity is Player && entity != player) {
                val targetLocation = entity.eyeLocation
                val vectorToTarget = targetLocation.toVector().subtract(eyeLocation.toVector())
                if (direction.angle(vectorToTarget) < Math.toRadians(30.0)) {
                    return entity
                }
            }
        }
        return null
    }
}