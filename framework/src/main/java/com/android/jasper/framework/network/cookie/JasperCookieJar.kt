package com.android.jasper.framework.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

/**
 *@author   Jasper
 *@create   2020/6/4 17:17
 *@describe
 *@update
 */
interface IJasperCookieJar : CookieJar {
    companion object {
        /**
         * 判断cookie是否已过期
         * @param cookie
         * @return
         */
        fun checkCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt < System.currentTimeMillis()
        }
    }

    /**
     * 清除cookie
     */
    fun clear()
}


class JasperCookieJar(private val cookieCache: ICookieCache) : IJasperCookieJar {
    override fun clear() {
        cookieCache.clear()
    }


    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        synchronized(this) {
            val removeCookieList = mutableListOf<Cookie>()
            val resultCookieList = mutableListOf<Cookie>()
            cookieCache.forEach { cookie ->
                if (IJasperCookieJar.checkCookieExpired(cookie)) {
                    removeCookieList.add(cookie)
                } else if (cookie.matches(url)) {
                    resultCookieList.add(cookie)
                }
            }
            cookieCache.removeAll(removeCookieList)
            return resultCookieList
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(this) { cookieCache.saveAll(cookies) }
    }


}