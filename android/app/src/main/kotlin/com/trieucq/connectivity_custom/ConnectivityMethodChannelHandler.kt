package com.trieucq.connectivity_custom

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * The handler receives [MethodCall]s from the UIThread, gets the related information from
 * a [Connectivity], and then sends the result back to the UIThread through the [MethodChannel].
 */
internal class ConnectivityMethodChannelHandler(private val connectivity: Connectivity) : MethodChannel.MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "check" -> {
                try {
                    // Kiểm tra xem connectivity có null không, nếu không thì trả về networkTypes
                    val networkTypes = connectivity.networkTypes
                    result.success(networkTypes)
                } catch (e: Exception) {
                    // Xử lý lỗi nếu có
                    result.error("ERROR", "Failed to get network types: ${e.message}", null)
                }
            }

            else -> result.notImplemented()
        }
    }
}
