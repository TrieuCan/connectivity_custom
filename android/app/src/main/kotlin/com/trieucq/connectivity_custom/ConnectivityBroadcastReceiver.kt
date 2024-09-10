package com.trieucq.connectivity_custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink

/**
 * The ConnectivityBroadcastReceiver receives connectivity updates and sends them to the UI thread
 * through an [EventChannel.EventSink].
 */
class ConnectivityBroadcastReceiver(
        private val context: Context,
        private val connectivity: Connectivity
) : BroadcastReceiver(), EventChannel.StreamHandler {
    private var events: EventSink? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var networkCallback: NetworkCallback? = null

    override fun onListen(arguments: Any?, events: EventSink?) {
        this.events = events
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    sendEvent(connectivity.getCapabilitiesFromNetwork(network))
                }

                override fun onCapabilitiesChanged(
                        network: Network, networkCapabilities: NetworkCapabilities
                ) {
                    sendEvent(connectivity.getCapabilitiesList(networkCapabilities))
                }

                override fun onLost(network: Network) {
                    sendCurrentStatusWithDelay()
                }
            }
            connectivity.getConnectivityManager().registerDefaultNetworkCallback(networkCallback!!)
        } else {
            context.registerReceiver(this, IntentFilter(CONNECTIVITY_ACTION))
        }
        // Emit initial event
        sendEvent(connectivity.networkTypes)
    }

    override fun onCancel(arguments: Any?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback?.let {
                connectivity.getConnectivityManager().unregisterNetworkCallback(it)
            }
            networkCallback = null
        } else {
            try {
                context.unregisterReceiver(this)
            } catch (e: Exception) {
                // Handle or log the exception
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Only send event if there are subscribers
        events?.success(connectivity.networkTypes)
    }

    private fun sendEvent(networkTypes: List<String>) {
        events?.let {
            mainHandler.post { it.success(networkTypes) }
        }
    }

    private fun sendCurrentStatusWithDelay() {
        mainHandler.postDelayed({
            events?.success(connectivity.networkTypes)
        }, 500)
    }

    companion object {
        private const val CONNECTIVITY_ACTION: String = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}
