package com.trieucq.connectivity_custom

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // Đăng ký plugin của bạn
        flutterEngine.plugins.add(ConnectivityPlugin())
    }
}
