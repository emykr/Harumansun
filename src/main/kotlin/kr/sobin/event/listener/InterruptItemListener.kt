package kr.sobin.event.listener

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kr.sobin.event.InterruptEventManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class InterruptItemListener(private val plugin: JavaPlugin) : Listener {
    private val eventManager = InterruptEventManager(plugin)

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        plugin.logger.info("[DEBUG] PlayerInteractEvent 발생: ${event.action}")

        // 우클릭 체크
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            plugin.logger.info("[DEBUG] 우클릭이 아님, 이벤트 무시")
            return
        }

        val player = event.player
        val item = event.item ?: return

        plugin.logger.info("[DEBUG] 플레이어: ${player.name}, 아이템: ${item.type}")

        if (!isValidInterruptTicket(item)) {
            plugin.logger.info("[DEBUG] 유효하지 않은 방해권")
            return
        }

        plugin.logger.info("[DEBUG] 유효한 방해권 확인됨, 타겟 탐색 시작")
        val target = findTargetPlayer(player, 5.0)
        val finalTarget = if (target != null) target else player
        plugin.logger.info("[DEBUG] 타겟 플레이어: ${finalTarget.name}")

        // 이벤트 매니저를 통해 랜덤 효과 적용
        val result = eventManager.applyRandomEffect(finalTarget)
        plugin.logger.info("[DEBUG] 이벤트 적용 결과: $result")

        if (result != null) {
            consumeTicket(item, player)
            sendEffectMessage(player, finalTarget, result)
            event.isCancelled = true
        }
    }

    private fun findTargetPlayer(player: Player, range: Double): Player? {
        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction

        return player.getNearbyEntities(range, range, range)
            .filterIsInstance<Player>()
            .filter { it != player }
            .map { entity ->
                val angle: Double = direction.angle(
                    entity.eyeLocation.toVector().subtract(eyeLocation.toVector())
                ).toDouble()
                Pair(entity, angle)
            }
            .filter { it.second <= Math.toRadians(30.0) }
            .minByOrNull { it.second }
            ?.first
    }

    private fun isValidInterruptTicket(item: ItemStack): Boolean {
        if (item.type != Material.PAPER) {
            plugin.logger.info("[DEBUG] 아이템이 종이가 아님")
            return false
        }

        val meta = item.itemMeta ?: return false
        if (!meta.hasDisplayName()) {
            plugin.logger.info("[DEBUG] 아이템에 이름이 없음")
            return false
        }

        val configItemName = plugin.config.getString("방해권.이름") ?: "&l&5랜덤 방해권"
        val expectedDisplayName = org.bukkit.ChatColor.translateAlternateColorCodes('&', configItemName)

        // 포맷 코드를 제거하고 비교
        val strippedItemName = org.bukkit.ChatColor.stripColor(meta.displayName) ?: ""
        val strippedExpectedName = org.bukkit.ChatColor.stripColor(expectedDisplayName) ?: ""

        plugin.logger.info("[DEBUG] 아이템 이름 비교 (스트립): '$strippedItemName' == '$strippedExpectedName'")
        plugin.logger.info("[DEBUG] 아이템 이름 비교 (원본): '${meta.displayName}' == '$expectedDisplayName'")

        return strippedItemName == strippedExpectedName
    }

    private fun consumeTicket(item: ItemStack, player: Player) {
        if (item.amount > 1) {
            item.amount -= 1
        } else {
            player.inventory.removeItem(item)
        }
    }

    private fun sendEffectMessage(player: Player, target: Player, effect: String) {
        val messages = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
            java.io.File(plugin.dataFolder, "messages.yml")
        )
        val rawMsg = messages.getString("effect_applied")
            ?: "[알림] {target}에게 {effect} 효과를 걸었습니다!"
        val msg = rawMsg
            .replace("{target}", target.name)
            .replace("{effect}", effect)
        player.sendMessage(
            Component.text(msg).color(NamedTextColor.RED)
        )
    }
}
