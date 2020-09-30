package com.android.jasper.framework.network.cookie

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie

/**
 *@author   Jasper
 *@create   2020/6/4 17:09
 *@describe
 *@update
 */

/**
 * cookie缓存基类
 */
interface ICookieCache : Iterable<Cookie> {
    /**
     * 保存cookie
     *
     * @param cookieCollection cookies
     */
    fun saveAll(cookieCollection: Collection<Cookie>)

    /**
     * 移除cookie
     *
     * @param cookieCollection cookies
     */
    fun removeAll(cookieCollection: Collection<Cookie>)

    /**
     * 清除cookie
     */
    fun clear()
}

/**
 * cookie缓存到内存
 * @property mCookieSets MutableSet<JasperCookie>
 */
class MemoryCookieCache : ICookieCache {
    private val mCookieSets: MutableSet<JasperCookie> = hashSetOf()
    override fun saveAll(cookieCollection: Collection<Cookie>) {
        val cookieList = JasperCookie.decorateAll(cookieCollection)
        mCookieSets.removeAll(cookieList)
        mCookieSets.addAll(cookieList)
    }

    override fun removeAll(cookieCollection: Collection<Cookie>) {
        val cookieList = JasperCookie.decorateAll(cookieCollection)
        mCookieSets.removeAll(cookieList)
    }

    override fun clear() {
        mCookieSets.clear()
    }

    override fun iterator(): Iterator<Cookie> {
        return CookieIterator(mCookieSets.iterator())
    }

    private inner class CookieIterator constructor(
        private val cookies: MutableIterator<JasperCookie>
    ) : MutableIterator<Cookie> {
        override fun hasNext(): Boolean {
            return cookies.hasNext()
        }

        override fun next(): Cookie {
            return cookies.next().cookie
        }

        override fun remove() {
            cookies.remove()
        }

    }
}


class SpCookieCache constructor(context: Context) : ICookieCache {
    companion object {
        fun createCookieKey(cookie: Cookie): String? {
            return (if (cookie.secure) "https" else "http") + "://" + cookie.domain + cookie.path + "|" + cookie.name
        }
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            "jasperPersistentCookie",
            Context.MODE_PRIVATE
        )
    }
    private val memoryCookieCache: MemoryCookieCache by lazy {
        MemoryCookieCache().apply {
            val spCookieList = mutableListOf<Cookie>()
            sharedPreferences.all.entries.forEach { spEntrie ->
                val serializedCookie = (spEntrie as? Map.Entry<*, *>)?.value as? String
                serializedCookie?.let {
                    SerializableCookie().decodeCookie(it)?.let { cookie ->
                        spCookieList.add(cookie)
                    }

                }
            }
            saveAll(spCookieList)

        }

    }


    override fun saveAll(cookieCollection: Collection<Cookie>) {
        memoryCookieCache.saveAll(cookieCollection)
        val editor: SharedPreferences.Editor = this.sharedPreferences.edit()
        cookieCollection.forEach { cookie ->
            if (cookie.persistent) {
                editor.putString(createCookieKey(cookie), SerializableCookie().encodeCookie(cookie))
            }
        }
        editor.apply()
    }

    override fun removeAll(cookieCollection: Collection<Cookie>) {
        memoryCookieCache.removeAll(cookieCollection)
        val editor: SharedPreferences.Editor = this.sharedPreferences.edit()
        cookieCollection.forEach {
            editor.remove(createCookieKey(it))
        }
        editor.apply()
    }

    override fun clear() {
        this.memoryCookieCache.clear()
        this.sharedPreferences.edit().clear().apply()
    }

    override fun iterator(): Iterator<Cookie> {
        return memoryCookieCache.iterator()
    }

}