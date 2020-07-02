package com.android.jasper.framework.network.cookie

import okhttp3.Cookie
import java.util.*

/**
 *@author   Jasper
 *@create   2020/6/4 16:57
 *@describe
 *@update
 */
class JasperCookie constructor( val cookie: Cookie) {
    companion object {
        internal fun decorateAll(cookieCollection: Collection<Cookie>): MutableList<JasperCookie> {
            val jasperCookieList = ArrayList<JasperCookie>(cookieCollection.size)
            for (cookie in cookieCollection) {
                jasperCookieList.add(JasperCookie(cookie))
            }
            return jasperCookieList
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other.javaClass == this.javaClass) {
            false
        } else {
            val otherCookie: JasperCookie = other as JasperCookie
            var result = this.cookie.name == otherCookie.cookie.name
            result = result && this.cookie.domain == otherCookie.cookie.domain
            result = result && this.cookie.path == otherCookie.cookie.path
            result = result && this.cookie.secure == otherCookie.cookie.secure
            result = result && this.cookie.hostOnly == otherCookie.cookie.hostOnly
            result
        }
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + this.cookie.name.hashCode()
        hash = 31 * hash + this.cookie.domain.hashCode()
        hash = 31 * hash + if (this.cookie.secure) 1 else 0
        hash = 31 * hash + if (this.cookie.hostOnly) 1 else 0
        return hash
    }
}