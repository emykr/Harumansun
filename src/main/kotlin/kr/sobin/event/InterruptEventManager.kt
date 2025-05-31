package kr.sobin.event

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class InterruptEventManager(private val plugin: JavaPlugin) {
    private val events = mutableListOf<AbstractInterruptEvent>()

    init {
        // 기본 이벤트들 등록
        registerEvent(BlindnessInterruptEvent(plugin))
        registerEvent(JailInterruptEvent(plugin))
        registerEvent(LightningInterruptEvent(plugin))
        registerEvent(FishInterruptEvent(plugin))
    }

    fun registerEvent(event: AbstractInterruptEvent) {
        events.add(event)
    }

    fun applyRandomEffect(target: Player): String? {
        // 각 이벤트의 확률 계산
        val eventWithChances = events.map { event ->
            event to event.getChance()
        }

        val totalChance = eventWithChances.sumOf { it.second }
        if (totalChance <= 0) return null

        val rand = Random.nextInt(totalChance)
        var accumulator = 0

        // 누적 확률로 이벤트 선택
        for ((event, chance) in eventWithChances) {
            accumulator += chance
            if (rand < accumulator) {
                if (event.apply(target)) {
                    return event.displayName
                }
                break
            }
        }

        return null
    }
}
