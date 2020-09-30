package com.android.jasper.framework.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.jasper.framework.util.NetworkUtils


/**
 *@author   Jasper
 *@create   2020/8/5 11:43
 *@describe
 *@update
 */
interface NetworkChangeListener {

    /**
     *
     * @param isConnected Boolean 网络是否连接
     * @param networkType NetworkType 网络类型
     */
    fun onChanged(isConnected: Boolean, networkType: NetworkUtils.NetworkType)
}

internal interface NetworkCallbackListener {
    val networkChangeList: MutableList<NetworkChangeListener>

}

@Suppress("DEPRECATION")
internal class NetworkStateReceiver constructor(private val context: Context) : BroadcastReceiver(),
    NetworkCallbackListener {
    init {
        val intentFilter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        context.registerReceiver(this, intentFilter)
    }

    private val networkChangeListenerList by lazy { mutableListOf<NetworkChangeListener>() }
    override val networkChangeList: MutableList<NetworkChangeListener>
        get() = networkChangeListenerList

    override fun onReceive(cnt: Context?, intent: Intent?) {
        intent?.let {
            if (ConnectivityManager.CONNECTIVITY_ACTION == it.action) {
                val networkType = NetworkUtils.getNetworkType(context)
                networkChangeList.forEach { changeListener ->
                    changeListener.onChanged(networkType != NetworkUtils.NetworkType.NONE, networkType)
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class NetworkCallbackImpl constructor(private val context: Context) : ConnectivityManager.NetworkCallback(),
    NetworkCallbackListener {
    private val networkChangeListenerList by lazy { mutableListOf<NetworkChangeListener>() }
    override val networkChangeList: MutableList<NetworkChangeListener>
        get() = networkChangeListenerList

    init {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
                ?.registerNetworkCallback(NetworkRequest.Builder().build(), this)
    }

    /**
     * 网络连接回调
     * @param network Network
     */
    override fun onAvailable(network: Network) {
        super.onAvailable(network)

    }

    override fun onLost(network: Network) {
        super.onLost(network)
        networkChangeList.forEach {
            it.onChanged(false, NetworkUtils.NetworkType.NONE)
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val networkType = NetworkUtils.getNetworkType(context)
        networkChangeList.forEach {
            it.onChanged(networkType != NetworkUtils.NetworkType.NONE, networkType)
        }
    }

}


