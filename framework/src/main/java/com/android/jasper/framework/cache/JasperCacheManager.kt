package com.android.jasper.framework.cache

import android.content.Context
import com.android.jasper.framework.base.SingletonHolder

/**
 *@author   Jasper
 *@create   2020/8/4 09:39
 *@describe 缓存管理器
 *@update
 */
class JasperCacheManager private constructor(private val context: Context) {
    internal val internalCache by lazy { InternalCache(context) }

    companion object : SingletonHolder<JasperCacheManager, Context>(::JasperCacheManager)


    /**
     *
     * @param key String
     * @param value String
     * @param aliveTimeSecond Long 有效期(秒) <=0 表示永久有效
     */
    fun saveInternalCache(key: String, value: String, aliveTimeSecond: Long = -1L) {
        internalCache.save(key, value, aliveTimeSecond)
    }

    fun getInternalCache(key: String, defaultValue: String = ""): String {
        return internalCache.get(key, defaultValue)
    }


    fun removeInternalCache(key: String) {
        internalCache.remove(key)
    }

    fun clear() {
        internalCache.clear()
    }

}