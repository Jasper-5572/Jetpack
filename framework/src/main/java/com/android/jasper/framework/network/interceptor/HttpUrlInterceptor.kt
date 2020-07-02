package com.android.jasper.framework.network.interceptor

import com.android.jasper.framework.network.NetworkConstants
import com.android.jasper.framework.network.NetworkUrlname
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 *@author   Jasper
 *@create   2020/6/8 17:27
 *@describe 根据header urlname动态修改url拦截器
 *@update
 */
class HttpUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //获取原始的originalRequest
        val originalRequest: Request = chain.request()
        val urlnameHead = NetworkConstants.URLNAME_HEAD
        //获取头信息的集合如
        originalRequest.headers(urlnameHead).let { urlnameHeadList ->
            if (urlnameHeadList.isNotEmpty()) {
                //获取originalRequest的创建者builder
                val builder: Request.Builder = originalRequest.newBuilder()
                //删除原有配置中的值,就是namesAndValues集合里的值
                builder.removeHeader(urlnameHead)
                val httpUrl =
                    NetworkUrlname.INSTANCE.getAddressByUrlname(urlnameHead[0].toString())
                        ?.toHttpUrlOrNull()
                if (httpUrl != null) {
                    //重建新的HttpUrl，需要重新设置的url部分
                    //获取处理后的新newRequest
                    val newRequest: Request = builder.url(
                        originalRequest.url.newBuilder()
                            //http协议如：http或者https
                            .scheme(httpUrl.scheme)
                            //主机地址
                            .host(httpUrl.host)
                            //端口
                            .port(httpUrl.port)
                            .build()
                    ).build()
                    return chain.proceed(newRequest)
                }
            }
        }
        return chain.proceed(originalRequest)
    }

}