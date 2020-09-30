package com.android.jasper.framework.network

import com.android.jasper.framework.JasperConfigurationManager
import com.android.jasper.framework.expansion.parseLong
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.network.cookie.JasperCookieJar
import com.android.jasper.framework.network.cookie.MemoryCookieCache
import com.android.jasper.framework.network.interceptor.LoggerInterceptor
import com.android.jasper.framework.util.OkHttpCertificateUtils
import okhttp3.OkHttpClient
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 *@author   Jasper
 *@create   2020/6/4 16:53
 *@describe
 *@update
 */
internal class JasperOkHttp private constructor() {
    val okHttpClient by lazy {
        val builder = getOkHttpClientBuilder().cookieJar(JasperCookieJar(MemoryCookieCache()))
        OkHttpCertificateUtils.trustAllCertificate(builder)
        //添加自定义OkHttp参数
        JasperConfigurationManager.INSTANCE.jasperConfiguration?.let {
            it.addOkHttpConfig(builder)
        }
        //添加日志拦截器
        if (JasperFramework.appDebug) {
            builder.addInterceptor(LoggerInterceptor())
        } else {
            //禁止代理
            builder.proxy(Proxy.NO_PROXY)
        }
        builder.build()
    }

    companion object {
        val INSTANCE by lazy { JasperOkHttp() }
        fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            val connectTimeout =
                    JasperConfigurationManager.INSTANCE.getValue(JasperNetwork.CONNECT_TIMEOUT_KEY).parseLong()
            val readTimeout =
                    JasperConfigurationManager.INSTANCE.getValue(JasperNetwork.READ_TIMEOUT_KEY).parseLong()
            val writeTimeout =
                    JasperConfigurationManager.INSTANCE.getValue(JasperNetwork.WRITE_TIMEOUT_KEY).parseLong()
            return OkHttpClient.Builder()
                    //链接超时时间
                    .connectTimeout(
                            if (connectTimeout <= 0) 10000 else connectTimeout,
                            TimeUnit.MILLISECONDS
                    )
                    .readTimeout(if (readTimeout <= 0) 10000 else readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(if (writeTimeout <= 0) 10000 else writeTimeout, TimeUnit.MILLISECONDS)
        }
    }
}