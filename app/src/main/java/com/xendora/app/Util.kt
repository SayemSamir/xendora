package com.xendora.app

import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.Collections

object Util {

    /** Strips any path parts and unsafe characters so a file can never escape the Downloads folder. */
    fun sanitizeFileName(raw: String): String {
        val base = raw.substringAfterLast('/').substringAfterLast('\\')
        val cleaned = base
            .map { c ->
                if (c.isLetterOrDigit() || c == '.' || c == '-' || c == '_' || c == ' ' || c == '(' || c == ')') c
                else '_'
            }
            .joinToString("")
            .trim()
            .trimStart('.')
        val name = if (cleaned.isBlank()) "file" else cleaned
        return name.take(120)
    }

    /** Best-effort LAN IPv4 address, preferring Wi-Fi interfaces. */
    fun localIpv4(): String? = try {
        Collections.list(NetworkInterface.getNetworkInterfaces())
            .filter { it.isUp && !it.isLoopback }
            .sortedBy { if (it.name.startsWith("wlan")) 0 else 1 }
            .flatMap { Collections.list(it.inetAddresses) }
            .firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
            ?.hostAddress
    } catch (e: Exception) {
        null
    }

    fun newDigest(): MessageDigest = MessageDigest.getInstance("SHA-256")

    fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    fun humanSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var i = 0
        while (value >= 1024 && i < units.size - 1) {
            value /= 1024
            i++
        }
        return if (i == 0) "$bytes B" else "%.2f %s".format(value, units[i])
    }
}
