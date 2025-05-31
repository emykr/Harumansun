package kr.sobin

import kr.sobin.command.HarumansunCommand
import kr.sobin.event.listener.InterruptItemListener
import kr.sobin.event.listener.InterrupterVillagerListener
import kr.sobin.npc.PriceUpdater
import kr.sobin.event.listener.PufferFishVillagerListener
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Harumansun : JavaPlugin() {
    private var initialized = false
    private lateinit var priceUpdater: PriceUpdater
    private val version = "1.0.0"

    // messages.yml 핸들링용 필드 추가
    lateinit var messages: YamlConfiguration
        private set

    override fun onEnable() {
        if (initialized) {
            logger.warning("이미 초기화된 상태입니다. 중복 초기화를 방지합니다.")
            return
        }
        try {
            val serverName = server.javaClass.name
            val isModded = kr.sobin.material.CompatUtil.isModdedServer()

            // config.yml 마이그레이션
            val configFile = File(dataFolder, "config.yml")
            if (configFile.exists()) {
                logger.info("config.yml 로드중...")
                try {
                    // 기존 config 로드
                    val oldConfig = config.saveToString()
                    val oldVersion = config.getString("버전") ?: "1.0.0"

                    // 버전이 다르면 마이그레이션
                    if (oldVersion != version) {
                        logger.info("버전 변경 감지: $oldVersion -> $version")
                        logger.info("config.yml 마이그레이션 시작...")

                        // 기본 config 저장 (새로운 기본값 포함)
                        saveDefaultConfig()

                        // 기존 config에서 값 로드
                        val oldConfiguration = YamlConfiguration.loadConfiguration(
                            java.io.StringReader(oldConfig)
                        )

                        // 새 config에 기존 값 적용 (버전은 제외)
                        for (key in oldConfiguration.getKeys(true)) {
                            if (!oldConfiguration.isConfigurationSection(key) && key != "버전") {
                                val value = oldConfiguration.get(key)
                                if (config.contains(key)) {  // 키가 새 config에도 있는 경우만 복사
                                    config.set(key, value)
                                }
                            }
                        }

                        // 새 버전 설정
                        config.set("버전", version)

                        // 변경된 config 저장
                        config.save(configFile)
                        logger.info("config.yml 마이그레이션 완료")
                    } else {
                        logger.info("현재 버전과 일치: $version")
                    }
                } catch (e: Exception) {
                    logger.severe("config.yml 로드/마이그레이션 중 오류 발생: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                saveDefaultConfig()
                config.set("버전", version)
                saveConfig()
                logger.info("새로운 config.yml 생성 완료")
            }

            // messages.yml 처리
            val messagesFile = File(dataFolder, "messages.yml")
            if (!messagesFile.exists()) {
                saveResource("messages.yml", false)
                logger.info("messages.yml이 존재하지 않아 기본 파일을 생성했습니다.")
            }
            messages = YamlConfiguration.loadConfiguration(messagesFile)

            priceUpdater = PriceUpdater(this)

            // 이벤트 리스너 등록
            server.pluginManager.registerEvents(InterrupterVillagerListener(this), this)
            server.pluginManager.registerEvents(InterruptItemListener(this), this)
            server.pluginManager.registerEvents(PufferFishVillagerListener(this), this)

            // 커맨드 등록 및 탭 완성
            getCommand("harumansun")?.apply {
                val cmd = HarumansunCommand(this@Harumansun)
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
