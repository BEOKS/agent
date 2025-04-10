package com.zayden.agent.util

import com.zayden.agent.logger
import org.springframework.stereotype.Component

@Component
class TotpGenerator {
    val log=logger()
    /**
     * 2차 인증을 위한 MFA 토큰을 생성합니다.
     * TOTP(Time-based One-Time Password) 알고리즘을 사용합니다.
     *
     * @param secretKey 2차 인증 시크릿 키 (Base32 인코딩된 문자열)
     * @return 생성된 MFA 토큰 (일반적으로 6자리 숫자)
     */
    fun generate(secretKey: String): String {
        try {
            // Base32로 인코딩된 시크릿 키를 디코딩
            val base32 = org.apache.commons.codec.binary.Base32()
            val bytes = base32.decode(secretKey.uppercase().replace(" ", ""))

            // 현재 시간을 기준으로 TOTP 카운터 계산 (30초 간격)
            val timeIndex = System.currentTimeMillis() / 1000 / 30

            // HMAC-SHA1을 사용한 해시 계산
            val mac = javax.crypto.Mac.getInstance("HmacSHA1")
            mac.init(javax.crypto.spec.SecretKeySpec(bytes, "HmacSHA1"))

            // 8바이트 카운터 생성
            val counter = ByteArray(8)
            var movingFactor = timeIndex
            for (i in 7 downTo 0) {
                counter[i] = (movingFactor and 0xff).toByte()
                movingFactor = movingFactor shr 8
            }

            // 해시 계산
            val hash = mac.doFinal(counter)

            // 동적 절단 (Dynamic Truncation)
            val offset = hash[hash.size - 1].toInt() and 0xf
            val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                    ((hash[offset + 1].toInt() and 0xff) shl 16) or
                    ((hash[offset + 2].toInt() and 0xff) shl 8) or
                    (hash[offset + 3].toInt() and 0xff)

            // 6자리 코드 생성
            val otp = binary % 1000000

            // 항상 6자리가 되도록 앞에 0 채우기
            val format = String.format("%06d", otp)
            log.debug("MFA 토큰 생성 완료: $format")
            return format
        } catch (e: Exception) {
            throw IllegalArgumentException("MFA 토큰 생성 중 오류가 발생했습니다: ${e.message}", e)
        }
    }
}
