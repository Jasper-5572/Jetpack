package com.android.jasper.framework.util

import android.util.Log
import com.android.jasper.framework.JasperConfigurationManager
import com.android.jasper.framework.JasperFramework

/**
 *@author   Jasper
 *@create   2020/4/10 14:00
 *@describe Log工具
 *@update
 */
object LogUtils {
    private val debug by lazy {
        "true".equals(
            JasperConfigurationManager.INSTANCE.getValue("logDebug").trim { it <= ' ' }, ignoreCase = true
        )
    }

    @JvmStatic
    fun v(vararg s: String) {
        if (debug) {
            v(JasperFramework::class.java, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun d(vararg s: String) {
        if (debug) {
            d(JasperFramework::class.java, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun i(vararg s: String) {
        if (debug) {
            i(JasperFramework::class.java, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun w(vararg s: String) {
        if (debug) {
            w(JasperFramework::class.java, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun e(vararg s: String) {
        if (debug) {
            e(JasperFramework::class.java, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun v(tClass: Class<*>, vararg s: String) {
        if (debug) {
            Log.v(tClass.simpleName, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun d(tClass: Class<*>, vararg s: String) {
        if (debug) {
            Log.d(tClass.simpleName, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun i(tClass: Class<*>, vararg s: String) {
        if (debug) {
            Log.i(tClass.simpleName, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun w(tClass: Class<*>, vararg s: String) {
        if (debug) {
            Log.w(tClass.simpleName, StringUtils.buildString(*s))
        }
    }

    @JvmStatic
    fun e(tClass: Class<*>, vararg s: String) {
        if (debug) {
            Log.e(tClass.simpleName, StringUtils.buildString(*s))
        }
    }
}