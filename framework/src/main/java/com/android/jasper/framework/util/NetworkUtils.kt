package com.android.jasper.framework.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings


/**
 *@author   Jasper
 *@create   2020/8/3 13:42
 *@describe
 *@update
 */
object NetworkUtils {
    enum class NetworkType {
        /**
         * 网络类型为wifi
         */
        WIFI,

        /**
         * 蜂窝网络
         */
        MOBILE,

        /**
         * 其他网络
         */
        OTHER,

        /**
         * 无网络连接
         */
        NONE
    }

    /**
     * 打开网络设置界面
     * @param context Context
     */
    fun openSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
    }

    /**
     * 判断网络是否连接
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     * @param context Context
     * @return Boolean
     */
    @Suppress("DEPRECATION")
    fun isConnected(context: Context): Boolean {
        return getNetworkType(context) != NetworkType.NONE
    }

    /**
     * 判断wifi是否连接状态
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     * @param context Context
     * @return Boolean
     */
    @Suppress("DEPRECATION")
    fun isWifiConnected(context: Context): Boolean {
        return getNetworkType(context) == NetworkType.WIFI
    }

    @Suppress("DEPRECATION")
    fun getNetworkType(context: Context): NetworkType {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.let { manager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.getNetworkCapabilities(manager.activeNetwork)?.let { networkCapabilities ->
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)){
                        return when {
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.OTHER
                            else-> NetworkType.NONE
                        }
                    }
                }

            } else {
                manager.activeNetworkInfo?.let { networkInfo ->
                    if (networkInfo.isConnected) {
                        when (networkInfo.type) {
                            ConnectivityManager.TYPE_MOBILE -> return NetworkType.MOBILE
                            ConnectivityManager.TYPE_WIFI -> return NetworkType.WIFI
                        }
                    }
                }
            }
        }
        return NetworkType.NONE
    }
}