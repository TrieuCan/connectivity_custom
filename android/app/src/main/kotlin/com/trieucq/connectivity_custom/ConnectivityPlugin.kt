package com.trieucq.connectivity_custom

import android.content.Context
import android.net.ConnectivityManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


/**
 * ConnectivityPlugin
 */
class ConnectivityPlugin : FlutterPlugin {
    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null
    private var receiver: ConnectivityBroadcastReceiver? = null

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        setupChannels(binding.binaryMessenger, binding.applicationContext)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        teardownChannels()
    }

    private fun setupChannels(messenger: BinaryMessenger, context: Context) {
        methodChannel = MethodChannel(messenger, "com.trieucq.connectivity_custom/connectivity")
        eventChannel = EventChannel(messenger, "com.trieucq.connectivity_custom/connectivity_status")
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val connectivity = Connectivity(connectivityManager, context)

        val methodChannelHandler = ConnectivityMethodChannelHandler(connectivity)
        receiver = ConnectivityBroadcastReceiver(context, connectivity)

        methodChannel?.setMethodCallHandler(methodChannelHandler)
        eventChannel?.setStreamHandler(receiver)
    }


    private fun teardownChannels() {
        methodChannel!!.setMethodCallHandler(null)
        eventChannel!!.setStreamHandler(null)
        receiver?.onCancel(null)
        methodChannel = null
        eventChannel = null
        receiver = null
    }
}