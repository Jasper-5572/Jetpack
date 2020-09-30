package com.android.jasper.framework.network.interceptor

import android.content.Context
import android.text.TextUtils
import androidx.annotation.CallSuper
import com.android.jasper.framework.cache.JasperCacheManager
import com.android.jasper.framework.expansion.parseLong
import com.android.jasper.framework.network.JasperNetwork
import com.android.jasper.framework.util.MD5Utils
import com.android.jasper.framework.util.NetworkUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

/**
 *@author   Jasper
 *@create   2020/8/3 16:19
 *@describe
 *@update
 */
open class JasperCacheInterceptor constructor(private val context: Context) : Interceptor {

    private val jasperCacheManager by lazy { JasperCacheManager.getInstance(context) }

    @CallSuper
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return request.header(JasperNetwork.CACHE_HEAD)?.let { cacheHeadValue ->
            //没有网络的时候才去读取缓存
            val cacheKey=getCacheKey(request)
            if (!NetworkUtils.isConnected(context)) {
                val cache = getResponseFromCache(cacheKey)
                if (!TextUtils.isEmpty(cache)) {
                    createResponseFromCache(request, cache)
                } else {
                    chain.proceed(request)
                }
            } else {
                //有网络永远读取网络
                val response = chain.proceed(request)
                if (response.isSuccessful) {
                    saveResponseToCache(cacheKey, response, cacheHeadValue.parseLong(-1))
                }
                response
            }
        } ?: let {
            chain.proceed(request)
        }
    }

    /**
     * 通过cache值创建Response
     * @param request Request
     * @param cache String
     * @return Response
     */
    protected open fun createResponseFromCache(request: Request, cache: String): Response {
        return Response.Builder()
                .request(request)
                .header("Hint", "response from cache")
                .body(cache.toResponseBody("application/json".toMediaTypeOrNull()))
                .protocol(Protocol.HTTP_1_1)
                .message("")
                .code(200)
                .build()
    }


    /**
     * 根据请求request获取对应的requestBody
     * @param request Request
     * @return String
     */
    protected fun getRequestBody(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val requestBody = buffer.readUtf8()
        buffer.close()
        return requestBody
    }

    open fun getCacheKey(request: Request): String {
        var cacheKey = request.url.toString()
        if (request.method.equals("POST", ignoreCase = true)) {
            cacheKey += getRequestBody(request)
        }
        return cacheKey
    }

    /**
     * 获取缓存数据
     * @param cacheKey String
     * @return String
     */
    open fun getResponseFromCache(cacheKey: String): String {
        val md5CacheKey = MD5Utils.getMD5(cacheKey)
        return jasperCacheManager.getInternalCache(md5CacheKey)
    }

    open  fun saveResponseToCache(cacheKey: String, response: Response, aliveTimeSecond: Long) {
        val responseString = response.body?.let { responseBody ->
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            source.buffer.clone().readUtf8()
        } ?: ""

        if (responseString.isNotEmpty()) {
            val md5CacheKey = MD5Utils.getMD5(cacheKey)
            jasperCacheManager.saveInternalCache(md5CacheKey, responseString, aliveTimeSecond)
        }

    }


}