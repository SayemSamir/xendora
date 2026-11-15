package io.xendora.app

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    companion object {
        private const val CHANNEL = "io.xendora/app"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "getDeviceId" -> {
                    result.success(android.os.Build.ID)
                }
                "getAndroidVersion" -> {
                    result.success(android.os.Build.VERSION.RELEASE ?: "Unknown")
                }
                "getSdkInt" -> {
                    result.success(android.os.Build.VERSION.SDK_INT)
                }
                "getDeviceModel" -> {
                    result.success("${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
}