package com.xendora.app

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

data class IncomingRequest(val fileName: String, val size: Long, val remote: String)

/**
 * Minimal LAN HTTP server.
 *  GET  /        -> browser sender page (assets/sender.html)
 *  POST /upload  -> streams one file to Downloads/XENDORA after pairing code + user approval.
 *
 * NOTE: v0.1 is plain HTTP. Traffic on the same Wi-Fi is NOT encrypted yet.
 */
class ReceiverServer(
    private val context: Context,
    private val pairing: PairingManager,
    private val onApprovalRequest: (IncomingRequest, (Boolean) -> Unit) -> Unit,
    private val onLog: (String) -> Unit
) {
    companion object {
        const val DEFAULT_PORT = 8787
        private const val MIN_FREE_BYTES = 100L * 1024 * 1024
        private const val APPROVAL_WAIT_MS = 35_000L
        private const val MAX_HEADER_LINE = 8192
    }

    private val pool = Executors.newCachedThreadPool()
    private val authLock = Any()

    @Volatile
    private var serverSocket: ServerSocket? = null

    @Volatile
    var port: Int = DEFAULT_PORT
        private set

    fun start() {
        if (serverSocket != null) return
        val ss = try {
            ServerSocket(DEFAULT_PORT)
        } catch (e: IOException) {
            ServerSocket(0)
        }
        serverSocket = ss
        port = ss.localPort
        onLog("Receiver chalu holo (port $port)")

        pool.execute {
            while (!ss.isClosed) {
                val client = try {
                    ss.accept()
                } catch (e: IOException) {
                    break
                }
                pool.execute { handleClient(client) }
            }
        }
    }

    fun stop() {
        val ss = serverSocket ?: return
        serverSocket = null
        try {
            ss.close()
        } catch (e: IOException) {
            // already closed
        }
        onLog("Receiver bondho holo")
    }

    private fun handleClient(socket: Socket) {
        socket.use { s ->
            try {
                s.soTimeout = 60_000
                val input = BufferedInputStream(s.getInputStream())
                val out = s.getOutputStream()
                handleRequest(input, out, s.inetAddress?.hostAddress ?: "unknown")
            } catch (e: IOException) {
                onLog("Connection bondho: ${e.message}")
            }
        }
    }

    private fun handleRequest(input: InputStream, out: OutputStream, remote: String) {
        val requestLine = readHeaderLine(input) ?: return respond(out, 400, "Bad request")

        val headers = HashMap<String, String>()
        while (true) {
            val line = readHeaderLine(input) ?: return respond(out, 400, "Bad request")
            if (line.isEmpty()) break
            val colon = line.indexOf(':')
            if (colon > 0 && headers.size < 64) {
                headers[line.substring(0, colon).trim().lowercase()] = line.substring(colon + 1).trim()
            }
        }

        val parts = requestLine.split(' ')
        if (parts.size < 2) return respond(out, 400, "Bad request")
        val method = parts[0]
        val path = parts[1].substringBefore('?')

        when {
            method == "GET" && (path == "/" || path == "/index.html") -> serveSenderPage(out)
            method == "POST" && path == "/upload" -> handleUpload(input, out, headers, remote)
            else -> respond(out, 404, "Not found")
        }
    }

    private fun serveSenderPage(out: OutputStream) {
        val html = context.assets.open("sender.html").use { it.readBytes() }
        writeResponse(out, 200, "text/html; charset=utf-8", html)
    }

    private fun handleUpload(
        input: InputStream,
        out: OutputStream,
        headers: Map<String, String>,
        remote: String
    ) {
        val length = headers["content-length"]?.toLongOrNull()
        if (length == null || length <= 0L) return respond(out, 411, "Content-Length dorkar")

        val decodedName = headers["x-filename"]?.let { raw ->
            try {
                URLDecoder.decode(raw, "UTF-8")
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        val fileName = Util.sanitizeFileName(decodedName ?: "file")
        val request = IncomingRequest(fileName, length, remote)

        val refusal = synchronized(authLock) {
            authorize(headers["x-pairing-code"], request)
        }
        if (refusal != null) return respond(out, refusal.first, refusal.second)

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/XENDORA")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return respond(out, 500, "File create kora jay nai")

        val hash = try {
            storeBody(input, uri, length, headers["x-sha256"]?.lowercase())
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            onLog("Transfer fail: ${e.message}")
            return respond(out, 422, "Transfer fail: ${e.message}")
        }

        onLog("Saved: $fileName (SHA-256 ${hash.take(16)}...)")
        writeResponse(
            out, 200, "application/json; charset=utf-8",
            "{\"status\":\"ok\",\"sha256\":\"$hash\"}".toByteArray(Charsets.UTF_8)
        )
    }

    /**
     * Runs under authLock. Checks the pairing code, free space and asks the user.
     * Returns null when approved, or (httpCode, message) when refused.
     */
    private fun authorize(code: String?, request: IncomingRequest): Pair<Int, String>? {
        when (pairing.check(code)) {
            PairingManager.Check.LOCKED -> return 429 to "Onek bhul code. 1 minute por abar chesta korun."
            PairingManager.Check.EXPIRED -> return 403 to "Code expire hoyeche. Receiver-e naya code nin."
            PairingManager.Check.WRONG -> return 403 to "Code bhul."
            PairingManager.Check.OK -> Unit
        }

        val free = StatFs(Environment.getExternalStorageDirectory().path).availableBytes
        if (free < request.size + MIN_FREE_BYTES) {
            return 507 to "Receiver-e storage kom ache."
        }

        val decision = AtomicBoolean(false)
        val latch = CountDownLatch(1)
        onApprovalRequest(request) { ok ->
            decision.set(ok)
            latch.countDown()
        }
        latch.await(APPROVAL_WAIT_MS, TimeUnit.MILLISECONDS)

        if (!decision.get()) return 403 to "Receiver reject korleo ba time out hoyeche."

        pairing.rotate() // one-time code: this approved transfer consumes it
        onLog("Approved: ${request.fileName} (${Util.humanSize(request.size)}) from ${request.remote}")
        return null
    }

    /** Streams the body to the MediaStore entry while hashing. Throws on any integrity problem. */
    private fun storeBody(input: InputStream, uri: Uri, length: Long, claimed: String?): String {
        val resolver = context.contentResolver
        val digest = Util.newDigest()
        var received = 0L

        val sink = resolver.openOutputStream(uri) ?: throw IOException("Output file khola jay nai")
        sink.use { os ->
            val buf = ByteArray(64 * 1024)
            var remaining = length
            while (remaining > 0) {
                val n = input.read(buf, 0, minOf(buf.size.toLong(), remaining).toInt())
                if (n < 0) break
                os.write(buf, 0, n)
                digest.update(buf, 0, n)
                received += n
                remaining -= n
            }
        }

        if (received != length) {
            throw IOException("File adha eshechhe ($received of $length bytes)")
        }
        val actual = Util.toHex(digest.digest())
        if (claimed != null && claimed != actual) {
            throw IOException("SHA-256 mile nai")
        }

        resolver.update(uri, ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }, null, null)
        return actual
    }

    private fun readHeaderLine(input: InputStream): String? {
        val buf = ByteArrayOutputStream()
        while (true) {
            val b = input.read()
            if (b < 0) {
                return if (buf.size() == 0) null else String(buf.toByteArray(), Charsets.ISO_8859_1)
            }
            if (b == '\n'.code) break
            if (b != '\r'.code) {
                if (buf.size() >= MAX_HEADER_LINE) return null
                buf.write(b)
            }
        }
        return String(buf.toByteArray(), Charsets.ISO_8859_1)
    }

    private fun respond(out: OutputStream, code: Int, message: String) {
        writeResponse(out, code, "text/plain; charset=utf-8", message.toByteArray(Charsets.UTF_8))
    }

    private fun writeResponse(out: OutputStream, code: Int, contentType: String, body: ByteArray) {
        val reason = when (code) {
            200 -> "OK"
            400 -> "Bad Request"
            403 -> "Forbidden"
            404 -> "Not Found"
            411 -> "Length Required"
            422 -> "Unprocessable Entity"
            429 -> "Too Many Requests"
            500 -> "Internal Server Error"
            507 -> "Insufficient Storage"
            else -> "Error"
        }
        val head = "HTTP/1.1 $code $reason\r\n" +
            "Content-Type: $contentType\r\n" +
            "Content-Length: ${body.size}\r\n" +
            "Connection: close\r\n" +
            "Cache-Control: no-store\r\n" +
            "X-Content-Type-Options: nosniff\r\n" +
            "X-Frame-Options: DENY\r\n" +
            "\r\n"
        out.write(head.toByteArray(Charsets.ISO_8859_1))
        out.write(body)
        out.flush()
    }
}
