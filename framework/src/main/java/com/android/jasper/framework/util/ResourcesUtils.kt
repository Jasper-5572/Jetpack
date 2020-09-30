@file:Suppress("CheckedExceptionsKotlin")

package com.android.jasper.framework.util

import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.android.jasper.framework.JasperFramework

/**
 *@author   Jasper
 *@create   2020/5/21 15:29
 *@describe
 *@update
 */
object ResourcesUtils {

    @JvmStatic
    fun getDisplayMetrics(): DisplayMetrics {
        return getResource().displayMetrics
    }

    /**
     * 获取 Resources
     *
     * @return Resources
     */
    @JvmStatic
    fun getResource(): Resources {
        return JasperFramework.INSTANCE.showActivity?.resources
                ?: JasperFramework.INSTANCE.application.resources
    }

    /**
     * 通过字符串id获取字符串
     *
     * @param stringRes 字符串id
     * @return 获取字符串
     */
    @JvmStatic
    fun getString(@StringRes stringRes: Int): String {
        return try {
            getResource().getString(stringRes)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    @JvmOverloads
    @ColorInt
    fun getColor(@ColorRes colorId: Int, theme: Resources.Theme? = null): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getResource().getColor(colorId, theme)
        } else getColor(colorId)
    }
}