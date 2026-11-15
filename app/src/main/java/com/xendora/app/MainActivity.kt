package com.xendora.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : Activity() {

    private val colBg = Color.parseColor("#0B1020")
    private val colCard = Color.parseColor("#141B2D")
    private val colCyan = Color.parseColor("#22D3EE")
    private val colBlue = Color.parseColor("#3B82F6")
    private val colViolet = Color.parseColor("#8B5CF6")
    private val colText = Color.parseColor("#E5E7EB")
    private val colMuted = Color.parseColor("#94A3B8")

    private val ui = Handler(Looper.getMainLooper())
    private val pairing = PairingManager()
    private val logLines = ArrayList<String>()

    private lateinit var server: ReceiverServer
    private lateinit var statusText: TextView
    private lateinit var addressText: TextView
    private lateinit var codeText: TextView
    private lateinit var timerText: TextView
    private lateinit var logText: TextView
    private lateinit var toggleButton: Button
    private var running = false

    private val ticker = object : Runnable {
        override fun run() {
            codeText.text = pairing.currentCode().chunked(3).joinToString(" ")
            val left = pairing.secondsLeft()
            val message = if (left > 0) "Code $left sec-e expire hobe" else "Code expire hoyeche. 'Naya code banan' chapun."
            timerText.text = message
            ui.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        server = ReceiverServer(
            context = applicationContext,
            pairing = pairing,
            onApprovalRequest = { req, decide -> askApproval(req, decide) },
            onLog = { msg -> appendLog(msg) }
        )
        setContentView(buildUi())
        ui.post(ticker)
        appendLog("XENDORA chalu holo. 'Receiver chalu korun' chapun.")
    }

    override fun onDestroy() {
        server.stop()
        ui.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun buildUi(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(48), dp(20), dp(28))
        }

        root.addView(makeText("XENDORA", 34f, colCyan, bold = true))
        root.addView(makeText("Universal File Transfer  ·  Share Beyond Limits.", 14f, colMuted), lp(2))
        root.addView(makeText("By Sayem Uddin Samir", 12f, colMuted), lp(2))

        val receiverCard = makeCard()
        receiverCard.addView(makeText("RECEIVE", 12f, colCyan, bold = true))
        statusText = makeText("Receiver: OFF", 18f, colText, bold = true)
        receiverCard.addView(statusText, lp(6))
        addressText = makeText("Chalu korle browser-e khular address ekhane dekhabe.", 14f, colMuted)
        receiverCard.addView(addressText, lp(8))
        codeText = makeText("------", 40f, colText, mono = true)
        receiverCard.addView(codeText, lp(16))
        timerText = makeText("", 13f, colMuted)
        receiverCard.addView(timerText, lp(4))

        toggleButton = makeButton("Receiver chalu korun", colBlue) { toggleServer() }
        receiverCard.addView(toggleButton, lp(16))

        val newCodeButton = makeButton("Naya code banan", colViolet) {
            pairing.rotate()
            appendLog("Naya pairing code toiri holo")
        }
        receiverCard.addView(newCodeButton, lp(8))
        root.addView(receiverCard, lp(18))

        val logCard = makeCard()
        logCard.addView(makeText("ACTIVITY", 12f, colCyan, bold = true))
        logText = makeText("", 13f, colMuted)
        logCard.addView(logText, lp(6))
        root.addView(logCard, lp(16))

        return ScrollView(this).apply {
            setBackgroundColor(colBg)
            isFillViewport = true
            addView(root)
        }
    }

    private fun toggleServer() {
        if (running) {
            server.stop()
            running = false
            statusText.text = "Receiver: OFF"
            addressText.text = "Receiver bondho. Abar chalu korle address dekhabe."
            toggleButton.text = "Receiver chalu korun"
        } else {
            server.start()
            running = true
            val ip = Util.localIpv4()
            statusText.text = "Receiver: ON"
            addressText.text = if (ip != null) {
                "Same Wi-Fi-te thakun. Sender browser-e khulben:\nhttp://$ip:${server.port}"
            } else {
                "IP paoa jay nai. Wi-Fi connect ache kina check korun."
            }
            toggleButton.text = "Receiver bondho korun"
        }
    }

    /** Shows Accept/Reject on the UI thread. Auto-rejects after 30 seconds. */
    private fun askApproval(req: IncomingRequest, decide: (Boolean) -> Unit) {
        ui.post {
            val answered = AtomicBoolean(false)
            val dialog = AlertDialog.Builder(this)
                .setTitle("Incoming file")
                .setMessage("${req.remote} theke:\n\n${req.fileName}\n${Util.humanSize(req.size)}\n\nGrahon korben?")
                .setCancelable(false)
                .setPositiveButton("Accept") { _, _ ->
                    if (answered.compareAndSet(false, true)) decide(true)
                }
                .setNegativeButton("Reject") { _, _ ->
                    if (answered.compareAndSet(false, true)) decide(false)
                }
                .create()
            dialog.show()

            ui.postDelayed({
                if (answered.compareAndSet(false, true)) {
                    decide(false)
                    dialog.dismiss()
                }
            }, 30_000L)
        }
    }

    private fun appendLog(message: String) {
        ui.post {
            val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            logLines.add(0, "$time  $message")
            while (logLines.size > 60) logLines.removeAt(logLines.size - 1)
            logText.text = logLines.joinToString("\n")
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun lp(topDp: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(topDp) }

    private fun makeText(
        value: String,
        sizeSp: Float,
        color: Int,
        bold: Boolean = false,
        mono: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        setTextColor(color)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
        typeface = when {
            mono -> Typeface.MONOSPACE
            bold -> Typeface.DEFAULT_BOLD
            else -> Typeface.DEFAULT
        }
    }

    private fun makeCard(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(16), dp(18), dp(16))
        background = GradientDrawable().apply {
            setColor(colCard)
            cornerRadius = dp(18).toFloat()
            setStroke(dp(1), 0x5522D3EE)
        }
    }

    private fun makeButton(caption: String, color: Int, onClick: () -> Unit): Button =
        Button(this).apply {
            text = caption
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = dp(14).toFloat()
            }
            setOnClickListener { onClick() }
        }
}
