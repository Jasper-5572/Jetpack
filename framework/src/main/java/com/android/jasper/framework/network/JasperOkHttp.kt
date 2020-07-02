package com.android.jasper.framework.network

import com.android.jasper.framework.JasperConfigurationManager
import com.android.jasper.framework.expansion.parseLong
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 *@author   Jasper
 *@create   2020/6/4 16:53
 *@describe
 *@update
 */
internal class JasperOkHttp private constructor() {
    companion object {
        /**
         * 配置文件 OkHttp连接超时时间（MILLISECONDS）
         */
        private const val CONNECT_TIMEOUT_KEY = "okHttpConnectTimeout"

        /**
         * 配置文件 OkHttp读取超时时间（MILLISECONDS）
         */
        private const val READ_TIMEOUT_KEY = "okHttpReadTimeout"

        /**
         * 配置文件 OkHttp写入超时时间（MILLISECONDS）
         */
        private const val WRITE_TIMEOUT_KEY = "okHttpWriteTimeout"


        val INSTANCE = JasperOkHttp().apply {

        }
    }




    internal fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val connectTimeout = JasperConfigurationManager.INSTANCE.getValue(CONNECT_TIMEOUT_KEY).parseLong()
        val readTimeout = JasperConfigurationManager.INSTANCE.getValue(READ_TIMEOUT_KEY).parseLong()
        val writeTimeout = JasperConfigurationManager.INSTANCE.getValue(WRITE_TIMEOUT_KEY).parseLong()
        return OkHttpClient.Builder()
            //链接超时时间
            .connectTimeout(if (connectTimeout <= 0) 10000 else connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(if (readTimeout <= 0) 10000 else readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(if (writeTimeout <= 0) 10000 else writeTimeout, TimeUnit.MILLISECONDS)
    }
}