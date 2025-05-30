package kr.sobin.nms

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

/**
 * 1.20.1 NMS/레지스트리 기반 모드 아이템 생성 구현체
 */
class NmsItemFactoryImpl : NmsItemFactory {
    override fun createModItem(modid: String, itemName: String, amount: Int): ItemStack? {
        if (modid.isBlank() || itemName.isBlank() || amount < 1) {
            Bukkit.getLogger().severe("[NmsItemFactory] 잘못된 파라미터: modid='$modid', itemName='$itemName', amount=$amount")
            return null
        }
        return try {
            val craftItemStackClass = Class.forName("org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack")
            val itemStackClass = Class.forName("net.minecraft.world.item.ItemStack")
            val itemClass = Class.forName("net.minecraft.world.item.Item")
            val resourceLocationClass = Class.forName("net.minecraft.resources.ResourceLocation")
            val registryClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries")

            // 필드명 확인 및 접근
            val itemRegistryField = registryClass.fields.firstOrNull { it.type.name.contains("Registry") }
                ?: throw NoSuchFieldException("Registry 필드를 찾을 수 없음")
            val itemRegistry = itemRegistryField.get(null)
            val resourceLocation = resourceLocationClass.getConstructor(String::class.java).newInstance("$modid:$itemName")

            // get(ResourceLocation) 메서드만 사용 (T 타입 반환)
            val getMethod = itemRegistry.javaClass.getMethod("get", resourceLocationClass)
            val nmsItem = getMethod.invoke(itemRegistry, resourceLocation)

            if (nmsItem == null) {
                Bukkit.getLogger().severe("[NmsItemFactory] NMS 레지스트리에서 모드 아이템을 찾을 수 없음: $modid:$itemName")
                return null
            }
            val nmsStack = itemStackClass.getConstructor(itemClass, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                .newInstance(nmsItem, amount, 0)
            val asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", itemStackClass)
            val bukkitStack = asBukkitCopy.invoke(null, nmsStack)
            if (bukkitStack is ItemStack) {
                Bukkit.getLogger().info("[NmsItemFactory] NMS 모드 아이템 생성 성공: $modid:$itemName ($amount)")
                bukkitStack
            } else {
                Bukkit.getLogger().severe("[NmsItemFactory] NMS 변환 실패: 반환값이 ItemStack이 아님 ($modid:$itemName, $amount)")
                null
            }
        } catch (e: Exception) {
            Bukkit.getLogger().severe("[NmsItemFactory] NMS 모드 아이템 생성 중 예외 발생: $modid:$itemName ($amount) - ${e.message}")
            null
        }
    }
}

