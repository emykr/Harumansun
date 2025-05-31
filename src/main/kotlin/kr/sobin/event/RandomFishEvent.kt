package kr.sobin.event

import dev.lone.itemsadder.api.CustomStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.get
import kotlin.random.Random

object RandomFishEvent {
    /**
     * 플레이어 전체 인벤토리(핫바, 메인, 아머, 오프핸드)에서
     * 바닐라+커스텀 물고기 중 1종을 무작위로 1개만 방생(제거)
     */
    fun releaseRandomFish(player: Player): String? {
        // 전체 인벤토리 아이템 모으기 (null 제거)
        val allItems = player.inventory.contents.toList() +
                player.inventory.armorContents.toList() +
                player.inventory.extraContents.toList()
        val nonNullItems = allItems.filterNotNull()

        // 실제로 가진 바닐라 물고기 종류
        val ownedVanilla = FishList.fishMaterials.filter { mat ->
            nonNullItems.any { it.type == mat && it.amount > 0 }
        }
        // 실제로 가진 커스텀 물고기 종류 (localizedName 기준)
        val ownedCustom = FishList.normalFishKeys.filter { key ->
            nonNullItems.any { it.itemMeta?.localizedName == key && it.amount > 0 }
        }

        if (ownedVanilla.isEmpty() && ownedCustom.isEmpty()) {
            return null
        }

        val allCandidates = ownedVanilla.map { it.name } + ownedCustom
        val randomKey = allCandidates[Random.nextInt(allCandidates.size)]

        // 실제 인벤토리의 모든 슬롯(핫바, 메인, 아머, 오프핸드)에서 제거
        fun removeOne(predicate: (ItemStack) -> Boolean): Boolean {
            val itemsToRemove = mutableListOf<Pair<ItemStack, Int>>()

            // 찾을 아이템 수집 (아이템과 슬롯 인덱스를 함께 저장)
            player.inventory.contents?.forEachIndexed { index, item ->
                if (item != null && predicate(item)) {
                    itemsToRemove.add(item to index)
                }
            }

            if (itemsToRemove.isEmpty()) return false

            // 랜덤으로 하나 선택해서 제거
            val (itemToRemove, slot) = itemsToRemove.random()

            // 현재 수량이 1개 이상이면 수량만 감소
            if (itemToRemove.amount > 1) {
                itemToRemove.amount = itemToRemove.amount - 1
            } else {
                // 1개만 있으면 슬롯을 비움
                player.inventory.setItem(slot, null)
            }
            player.updateInventory()
            return true
        }

        val released = if (ownedVanilla.any { it.name == randomKey }) {
            val mat = Material.valueOf(randomKey)
            val namespace = "minecraft:${randomKey.lowercase()}"
            if (removeOne { it.type == mat && it.amount > 0 }) {
                Bukkit.getLogger().info("[FishEvent] ${player.name}가 $namespace 물고기를 방생했습니다!")
                randomKey
            } else null
        } else {
            // 커스텀 물고기는 네임스페이스로 검사
            val namespace = "fishing_pack:$randomKey"
            if (removeOne { item ->
                val customStack = CustomStack.byItemStack(item)
                customStack?.namespacedID == namespace
            }) {
                Bukkit.getLogger().info("[FishEvent] ${player.name}가 $namespace 물고기를 방생했습니다!")
                randomKey
            } else null
        }

        if (released != null) {
                player.sendMessage(net.kyori.adventure.text.Component.text()
                .content("물고기 1마리가 방생되었습니다!")
                .color(net.kyori.adventure.text.format.NamedTextColor.RED)
                .build())
            return released
        }
        return null
    }
}

