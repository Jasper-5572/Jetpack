package com.android.jasper.framework.network

import com.android.jasper.framework.JasperFramework
import okhttp3.ResponseBody
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 *@author   Jasper
 *@create   2020/6/4 15:17
 *@describe
 *@update
 */

object NetworkConstants {

    /**
     * 在请求头里面添加urlname
     */
    const val URLNAME_HEAD: String = "urlname"

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
}
class JasperNetworkManager{

}

class NetworkUrlname private constructor() {

    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { NetworkUrlname() }
    }


    fun getAddressByUrlname(urlname: String): String? {

        return ""
    }
}

interface api {
    @Headers("${NetworkConstants.URLNAME_HEAD}:")
    @POST("members/auth")
    fun request(): ResponseBody
}