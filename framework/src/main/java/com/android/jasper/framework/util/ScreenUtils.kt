@file:Suppress("CheckedExceptionsKotlin", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.android.jasper.framework.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 *@author   Jasper
 *@create   2020/5/21 15:25
 *@describe 屏幕相关工具
 *@update
 */
object ScreenUtils {
    /**
     * 获得屏幕高度(px)
     * @return 屏幕高度 (px)
     */
    @JvmStatic
    fun getScreenHeight(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        (context?.applicationContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.let {
            it.defaultDisplay?.getMetrics(displayMetrics)
        }
        return displayMetrics.heightPixels
    }

    /**
     * 获得屏幕宽度(px)
     * @return 屏幕宽度(px)
     */
    @JvmStatic
    fun getScreenWidth(context: Context?): Int {
        val displayMetrics = DisplayMetrics()
        (context?.applicationContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.let {
            it.defaultDisplay?.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }

    /**
     * 获取状态栏高度（px）
     * @return 状态栏高度(px)
     */
    @JvmStatic
    fun getStatusHeight(): Int {
        var statusHeight = 0
        try {
            statusHeight = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
            )
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        if (statusHeight <= 0) {
            try {
                @SuppressLint("PrivateApi") val clazz =
                    Class.forName("com.android.internal.R\$dimen")
                val `object` = clazz.newInstance()
                val height = Integer.parseInt(
                    clazz.getField("status_bar_height").get(`object`).toString()
                )
                statusHeight = ResourcesUtils.getResource()?.getDimensionPixelSize(height) ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return statusHeight
    }


    /**
     * 获取虚拟按键的高度（px）
     *
     * @return 虚拟按键的高度（px）
     */
    @JvmStatic
    fun getNavigationBarHeight(context: Context?): Int {
        return getFullScreenHeight(context) - getScreenHeight(context)
    }

    /**
     * 获取整个屏幕的高度
     *
     * @return 屏幕的高度（包括虚拟按键 px）
     */
    @JvmStatic
    fun getFullScreenHeight(context: Context?): Int {
        var result = 0
        (context?.applicationContext?.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.let {
            try {
                val dm = DisplayMetrics()
                val method = Class.forName("android.view.Display")
                    .getMethod("getRealMetrics", DisplayMetrics::class.java)
                method.invoke(it.defaultDisplay, dm)
                result = dm.heightPixels
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}