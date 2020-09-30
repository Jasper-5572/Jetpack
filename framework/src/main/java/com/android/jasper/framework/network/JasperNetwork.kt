package com.android.jasper.framework.network

import android.os.Build
import com.android.jasper.framework.JasperConfigurationManager
import com.android.jasper.framework.JasperFramework
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import retrofit2.Retrofit
import com.android.jasper.framework.network.interceptor.HttpUrlInterceptor
import com.android.jasper.framework.network.interceptor.JasperCacheInterceptor
import com.android.jasper.framework.util.StringUtils

/**
 *@author   Jasper
 *@create   2020/6/4 15:17
 *@describe 网络相关
 *@update
 */
class JasperNetwork private constructor() {
    companion object {
        /**
         * 自定义缓存请求头 [JasperCacheInterceptor]
         */
        const val CACHE_HEAD = "Cache-JasperCustom"


        const val BASE_URL = "https://github.com/Jasper-5572/"

        /**
         * 在请求头里面添加urlname[HttpUrlInterceptor]
         */
        const val URLNAME_HEAD = "urlname"

        /**
         * 配置文件 OkHttp连接超时时间（MILLISECONDS）
         */
        const val CONNECT_TIMEOUT_KEY = "okHttpConnectTimeout"

        /**
         * 配置文件 OkHttp读取超时时间（MILLISECONDS）
         */
        const val READ_TIMEOUT_KEY = "okHttpReadTimeout"

        /**
         * 配置文件 OkHttp写入超时时间（MILLISECONDS）
         */
        const val WRITE_TIMEOUT_KEY = "okHttpWriteTimeout"
        val INSTANCE by lazy { JasperNetwork() }
    }

    private val retrofitMap by lazy { mutableMapOf<HttpUrl, Retrofit>() }

    private val networkCallbackImpl by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkCallbackImpl(JasperFramework.INSTANCE.application)
        } else {
            NetworkStateReceiver(JasperFramework.INSTANCE.application)
        }
    }

    /**
     *  根据url或者UrlName创建service
     * @param addressOrUrlName String
     * @param service Class<T>
     * @return T
     */
    @Synchronized
    fun <T> createApiService(addressOrUrlName: String, service: Class<T>): T {
        var url = addressOrUrlName
        if (!StringUtils.isLink(addressOrUrlName)) {
            url = getAddressByUrlname(addressOrUrlName)
        }
        val httpUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("Illegal URL: $url")
        val retrofit: Retrofit = (retrofitMap[httpUrl]) ?: createRetrofit(url)
        retrofitMap[httpUrl] = retrofit
        return retrofit.create(service)
    }

    @Synchronized
    private fun createRetrofit(url: String): Retrofit {
        return JasperRetrofit.INSTANCE.retrofitBuilder.baseUrl(url).build()
    }


    /**
     * 通过Urlname获取地址
     * @param urlname String
     * @return String
     */
    fun getAddressByUrlname(urlname: String): String {
        JasperConfigurationManager.INSTANCE.jasperConfiguration?.urlAdapter?.let {
            return it.getAddressByUrlname(urlname)
        } ?: let {
            return JasperConfigurationManager.INSTANCE.getValue(urlname)
        }
    }

    /**
     * 注册网络变化监听
     * @param listener NetworkChangeListener
     */
    fun registerNetworkChangeListener(listener: NetworkChangeListener){
        networkCallbackImpl.networkChangeList.add(listener)
    }

    /**
     *
     * @param listener NetworkChangeListener
     */
    fun unregisterNetworkChangeListener(listener: NetworkChangeListener){
        networkCallbackImpl.networkChangeList.remove(listener)
    }
}


/**
 * url适配器
 */
interface UrlAdapter {
    /**
     * 通过urlname获取地址
     * @param urlname String
     * @return String
     */
    fun getAddressByUrlname(urlname: String): String
}

