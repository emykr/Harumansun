package kr.sobin.material

import kr.sobin.nms.NmsItemFactoryImpl
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

object CompatUtil {
    /**
     * Mohist 전용: 항상 true 반환
     */
    fun isModdedServer(): Boolean = true

    /**
     * 1.20.1 Mohist/Forge 환경에서 NMS/레지스트리 기반 모드 아이템 생성
     */
    fun createModItem(modid: String, itemName: String, amount: Int = 1): ItemStack? {
        // NMS 팩토리 구현체 사용 (향후 확장성 위해 try-catch로 감싸고, 실패 시 null 반환)
        return try {
            NmsItemFactoryImpl().createModItem(modid, itemName, amount)
        } catch (e: Exception) {
            Bukkit.getLogger().severe("[CompatUtil] NMS/Forge 모드 아이템 생성 중 예외 발생: $modid:$itemName ($amount) - ${e.message}")
            null
        }
    }

    /**
     * 바닐라 아이템 생성 (Mohist 환경에서도 동작)
     */
    fun createVanillaItem(material: org.bukkit.Material, amount: Int = 1): ItemStack {
        return try {
            ItemStack(material, amount)
        } catch (e: Exception) {
            Bukkit.getLogger().severe("[CompatUtil] 바닐라 아이템 생성 실패: ${material.name} ($amount) - ${e.message}")
            ItemStack(org.bukkit.Material.STONE, 1)
        }
    }
}
