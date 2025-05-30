package kr.sobin

import kr.sobin.command.HarumansunCommand
import kr.sobin.material.FishTraderListener
import kr.sobin.npc.InterrupterVillagerListener
import kr.sobin.npc.PriceUpdater
import org.bukkit.plugin.java.JavaPlugin

class Harumansun : JavaPlugin() {
    private var initialized = false
    private lateinit var priceUpdater: PriceUpdater

    override fun onEnable() {
        if (initialized) {
            logger.warning("이미 초기화된 상태입니다. 중복 초기화를 방지합니다.")
            return
        }
        try {
            val serverName = server.javaClass.name
            val isModded = kr.sobin.material.CompatUtil.isModdedServer()
            logger.info("서버 종류: $serverName, 모드버킷 여부: $isModded")

            saveDefaultConfig()
            reloadConfig()
            priceUpdater = PriceUpdater(this)


            // 이벤트 리스너 등록
            server.pluginManager.registerEvents(FishTraderListener(), this)
            server.pluginManager.registerEvents(InterrupterVillagerListener(this), this)
            server.pluginManager.registerEvents(kr.sobin.npc.PufferFishVillagerListener(this), this)


            // 커맨드 등록 및 탭 완성
            getCommand("harumansun")?.apply {
                val cmd = HarumansunCommand(this@Harumansun)
                setExecutor(cmd)
                tabCompleter = cmd
            }
            getCommand("player")?.apply {
                val cmd = kr.sobin.command.PlayerCommand(this@Harumansun)
                setExecutor(cmd)
                tabCompleter = cmd
            }
            initialized = true
        } catch (e: Exception) {
            logger.severe("플러그인 초기화 중 예외 발생: ${e.message}")
            e.printStackTrace()
            server.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {
        logger.info("플러그인이 비활성화되었습니다.")
    }
}
