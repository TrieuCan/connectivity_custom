package com.trieucq.connectivity_custom

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class Connectivity(private val connectivityManager: ConnectivityManager, private val context: Context) {
    val networkTypes: List<String>
        @SuppressLint("MissingPermission")
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                return getCapabilitiesFromNetwork(network)
            } else {
                // For legacy versions, return a single type as before or adapt similarly if multiple types
                // need to be supported
                return networkTypesLegacy
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getCapabilitiesFromNetwork(network: Network?): List<String> {
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return getCapabilitiesList(capabilities)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getCapabilitiesList(capabilities: NetworkCapabilities?): List<String> {
        val types: MutableList<String> = ArrayList()
        if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            types.add(CONNECTIVITY_NONE)
            return types
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
            types.add(CONNECTIVITY_WIFI)

            // Kiểm tra tên mạng Wi-Fi (SSID)
            val wifiName: String = currentSsid
            types.add(wifiName)
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            types.add(CONNECTIVITY_ETHERNET)
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            types.add(CONNECTIVITY_VPN)
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            types.add(CONNECTIVITY_MOBILE)
        }
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            types.add(CONNECTIVITY_BLUETOOTH)
        }
        if (types.isEmpty() && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            types.add(CONNECTIVITY_OTHER)
        }
        if (types.isEmpty()) {
            types.add(CONNECTIVITY_NONE)
        }
        return types
    }

    @get:Suppress("deprecation")
    private val networkTypesLegacy: List<String>
        get() {
            // handle type for Android versions less than Android 6
            val info = connectivityManager.activeNetworkInfo
            val types: MutableList<String> = ArrayList()
            if (info == null || !info.isConnected) {
                types.add(CONNECTIVITY_NONE)
                return types
            }
            val type = info.type
            when (type) {
                ConnectivityManager.TYPE_BLUETOOTH -> types.add(CONNECTIVITY_BLUETOOTH)
                ConnectivityManager.TYPE_ETHERNET -> types.add(CONNECTIVITY_ETHERNET)
                ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX -> {
                    types.add(CONNECTIVITY_WIFI)

                    // Kiểm tra tên mạng Wi-Fi (SSID)
                    val wifiName: String = currentSsid
                    types.add(wifiName)
                }

                ConnectivityManager.TYPE_VPN -> types.add(CONNECTIVITY_VPN)
                ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_DUN, ConnectivityManager.TYPE_MOBILE_HIPRI -> types.add(CONNECTIVITY_MOBILE)
                else -> types.add(CONNECTIVITY_OTHER)
            }
            return types
        }

    @get:Suppress("deprecation")
    private val currentSsid: String
        get() {
            // Kiểm tra quyền ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return "Quyền vị trí chưa được cấp"
            }

            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null) {
                val ssid = wifiInfo.ssid
                return if (ssid != null && ssid != "<unknown ssid>") {
                    ssid.replace("\"", "")
                } else {
                    "SSID không xác định"
                }
            }
            return "Không thể lấy thông tin Wi-Fi"
        }

    fun getConnectivityManager(): ConnectivityManager {
        return connectivityManager
    }

    companion object {
        const val CONNECTIVITY_NONE: String = "none"
        const val CONNECTIVITY_WIFI: String = "wifi"
        const val CONNECTIVITY_MOBILE: String = "mobile"
        const val CONNECTIVITY_ETHERNET: String = "ethernet"
        const val CONNECTIVITY_BLUETOOTH: String = "bluetooth"
        const val CONNECTIVITY_VPN: String = "vpn"
        const val CONNECTIVITY_OTHER: String = "other"
    }
}
