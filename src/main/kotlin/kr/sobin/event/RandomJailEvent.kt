package kr.sobin.event

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RandomJailEvent {
    fun jailPlayer(target: Player) {
        // /jail <닉네임> 감옥 3m 명령 실행
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "jail ${target.name} 감옥 3m")

        // 로그 추가
        Bukkit.getLogger().info("[JailEvent] ${target.name}가 감옥에 갇혔습니다!")
    }
}

