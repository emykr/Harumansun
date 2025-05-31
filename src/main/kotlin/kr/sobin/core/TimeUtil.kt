package kr.sobin.core

object TimeUtil {
    /**
     * 문자열로 된 시간을 초 단위로 변환
     * 지원 포맷:
     * - 숫자만 있는 경우: 초 단위로 해석
     * - 숫자 + 단위(s, m, h, 초, 분, 시간)
     *
     * 예시:
     * - "60" -> 60초
     * - "1m" -> 60초
     * - "1분" -> 60초
     * - "1h" -> 3600초
     * - "1시간" -> 3600초
     */
    fun parseTime(timeStr: String): Int {
        val normalizedStr = timeStr.trim().lowercase()
        if (normalizedStr.isEmpty()) return 0

        return try {
            when {
                normalizedStr.endsWith("s") -> normalizedStr.removeSuffix("s").toInt()
                normalizedStr.endsWith("m") -> normalizedStr.removeSuffix("m").toInt() * 60
                normalizedStr.endsWith("h") -> normalizedStr.removeSuffix("h").toInt() * 3600
                normalizedStr.endsWith("초") -> normalizedStr.removeSuffix("초").toInt()
                normalizedStr.endsWith("분") -> normalizedStr.removeSuffix("분").toInt() * 60
                normalizedStr.endsWith("시간") -> normalizedStr.removeSuffix("시간").toInt() * 3600
                else -> normalizedStr.toInt() // 단위가 없으면 초로 해석
            }
        } catch (e: NumberFormatException) {
            0 // 파싱 실패 시 0 반환
        }
    }
}
