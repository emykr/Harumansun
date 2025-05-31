package kr.sobin.npc

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration

class PriceUpdater(private val plugin: JavaPlugin) {
    private var currentPrice = 100.0
    private val volatility = 0.2 // 20% 변동성

    init {
        startPriceUpdates()
        startBroadcastPrice()
    }

    private fun startPriceUpdates() {
        object : BukkitRunnable() {
            override fun run() {
                updatePrice()
            }
        }.runTaskTimer(plugin, 0L, 20L * 15) // 15초마다 가격 업데이트
    }

    private fun startBroadcastPrice() {
        // 전체 브로드캐스트 제거: 아무 동작도 하지 않음
    }

    private fun updatePrice() {
        val changePercent = (Random.nextDouble() - 0.5) * 2 * volatility
        val change = currentPrice * changePercent
        currentPrice += change

        // 최소 1원 보장
        if (currentPrice < 1) currentPrice = 1.0

        try {
            // 기존 config 불러오기
            val configFile = File(plugin.dataFolder, "config.yml")
            val config = YamlConfiguration.loadConfiguration(configFile)

            // 가격만 업데이트
            config.set("복어 상점.가격", currentPrice.toInt().toString())

            // 변경된 config 저장
            config.save(configFile)

            plugin.logger.info("[PriceUpdater] 복어 가격 업데이트: ${currentPrice.toInt()}원")
        } catch (e: Exception) {
            plugin.logger.severe("[PriceUpdater] 가격 업데이트 중 오류 발생: ${e.message}")
        }
    }
}
