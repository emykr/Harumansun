package kr.sobin.material

import kr.sobin.npc.FishTraderGUI
import kr.sobin.npc.InterrupterVillager
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


class FishTraderListener : Listener {
    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        val npc = event.npc
        val player = event.clicker
        // "물고기 상인" 이름의 NPC만 처리
        if (npc.name == "방해권 상인") {
            InterrupterVillager.openInterrupterShop(player)
        }
    }
}


