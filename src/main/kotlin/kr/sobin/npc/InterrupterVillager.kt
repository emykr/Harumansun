package kr.sobin.npc

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

class InterrupterVillager {
    companion object {

        /**
         * 방해권 상인 GUI를 여는 함수 (보라/노란 유리판, 이름 공백)
         */
        fun openInterrupterShop(player: Player) {
            val inv: Inventory = Bukkit.createInventory(null, 27, "방해권 상인")
            fun blankPane(material: Material): ItemStack {
                val item = ItemStack(material)
                val meta = item.itemMeta
                meta?.setDisplayName(" ") // 공백 한 칸으로 명확하게 처리
                item.itemMeta = meta
                return item
            }
            val purplePane = blankPane(Material.PURPLE_STAINED_GLASS_PANE)
            val yellowPane = blankPane(Material.YELLOW_STAINED_GLASS_PANE)
            val purpleSlots = listOf(0, 1, 2, 9, 10, 11, 18, 19, 20, 6, 7, 8, 15, 16, 17, 24, 25, 26)
            val yellowSlots = listOf(3, 4, 5, 12, 14, 21, 22, 23)
            for (slot in purpleSlots) {
                inv.setItem(slot, purplePane)
            }
            for (slot in yellowSlots) {
                inv.setItem(slot, yellowPane)
            }
            // 13번 슬롯: 랜덤 방해권 아이템 (Kyori Adventure 사용)
            val paper = ItemStack(Material.PAPER)
            val meta = paper.itemMeta
            val displayName = Component.text(
                "랜덤 방해권 ",
                NamedTextColor.DARK_PURPLE,
            ).decoration(TextDecoration.ITALIC, false)
            val lore = listOf(
                Component.text(
                    "\n" +
                            "사람을 바라보고 우클릭하면 해당 플레이어에게 랜덤한 방해가 작동되는 방해권 ",
                    NamedTextColor.YELLOW
                ).decoration(TextDecoration.ITALIC, false),
                Component.text("사람을 겨냥하지않고 우클릭하면 사용자 본인에게 작동하니 주의가 필요하다"
                            + "\n",
                    NamedTextColor.YELLOW
                ).decoration(TextDecoration.ITALIC, false)
            )
            if (meta is ItemMeta) {
                meta.displayName(displayName)
                meta.lore(lore)
            }
            paper.itemMeta = meta
            inv.setItem(13, paper)
            player.openInventory(inv)
        }

        fun playBuySound(player: Player, success: Boolean) {
            val sound = if (success) {
                "entity.experience_orb.pickup"
            } else {
                "entity.villager.no"
            }
            player.playSound(player.location, sound, 1.0f, 1.0f)
        }
    }
}

