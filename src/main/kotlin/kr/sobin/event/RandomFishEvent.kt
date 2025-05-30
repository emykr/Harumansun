package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random
import kr.sobin.event.FishList

object RandomFishEvent {
    /**
     * FishList의 일반 등급 물고기 리스트에서 랜덤으로 1종류 반환
     */
    fun pickRandomNormalFish(): String = FishList.normalFishKeys.random()

    /**
     * 플레이어 인벤토리에서 일반 물고기 여러 종류를 1개씩만 남기고 모두 제거(방생)
     * @param player 대상 플레이어
     * @return 방생된 물고기 종류(키)
     */
    fun releaseRandomFish(player: Player): String {
        val fishKey = pickRandomNormalFish()
        val inv = player.inventory
        var found = false
        for (slot in 0 until inv.size) {
            val item = inv.getItem(slot) ?: continue
            val typeKey = try { item.type.key } catch (e: Exception) { null }
            // 네임스페이스와 아이템 이름이 정확히 일치하는 경우만 방생 대상으로 함
            if (typeKey == null || typeKey.namespace != "fishing_pack" || typeKey.key != fishKey) continue
            if (!found) {
                if (item.amount > 1) item.amount = 1
                found = true
            } else {
                inv.clear(slot)
            }
        }
        return fishKey
    }
}

