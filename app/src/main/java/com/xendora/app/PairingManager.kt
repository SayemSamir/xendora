package com.xendora.app

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * One-time 6-digit pairing code with expiry and brute-force lockout.
 * The code is rotated after every approved transfer (one-time use).
 */
class PairingManager(private val ttlMillis: Long = 120_000L) {

    enum class Check { OK, WRONG, EXPIRED, LOCKED }

    private val random = SecureRandom()
    private var code = ""
    private var expiresAt = 0L
    private var failures = 0
    private var lockedUntil = 0L

    init {
        rotate()
    }

    @Synchronized
    fun currentCode(): String = code

    @Synchronized
    fun secondsLeft(): Long = maxOf(0L, (expiresAt - System.currentTimeMillis()) / 1000L)

    @Synchronized
    fun rotate() {
        code = "%06d".format(random.nextInt(1_000_000))
        expiresAt = System.currentTimeMillis() + ttlMillis
        failures = 0
    }

    @Synchronized
    fun check(candidate: String?): Check {
        val now = System.currentTimeMillis()
        if (now < lockedUntil) return Check.LOCKED
        if (now > expiresAt) return Check.EXPIRED

        val matches = MessageDigest.isEqual(
            code.toByteArray(Charsets.UTF_8),
            (candidate ?: "").toByteArray(Charsets.UTF_8)
        )
        if (matches) {
            failures = 0
            return Check.OK
        }

        failures++
        if (failures >= 5) {
            lockedUntil = now + 60_000L
            failures = 0
        }
        return Check.WRONG
    }
}
