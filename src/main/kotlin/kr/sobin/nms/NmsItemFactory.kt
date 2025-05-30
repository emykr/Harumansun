package kr.sobin.nms

import org.bukkit.inventory.ItemStack

/**
 * NMS/Forge 환경에서 모드 아이템을 생성하는 인터페이스
 */
interface NmsItemFactory {
    /**
     * 모드 아이템을 생성합니다.
     * @param modid 모드 id (예: aquaculture)
     * @param itemName 아이템 이름 (예: atlantic_cod)
     * @param amount 수량
     * @return 생성된 ItemStack, 실패 시 null
     */
    fun createModItem(modid: String, itemName: String, amount: Int = 1): ItemStack?
}

