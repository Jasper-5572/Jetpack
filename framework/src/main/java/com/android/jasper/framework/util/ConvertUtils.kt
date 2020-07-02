package com.android.jasper.framework.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

/**
 *@author   Jasper
 *@create   2020/5/21 15:33
 *@describe 单位转换工具
 *@update
 */
object ConvertUtils {

    /**
     * sp->px
     * @param spValue Float
     * @return Int
     */
    @JvmStatic
    fun sp2px(spValue: Float): Int {
        return (spValue * Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()
    }


    /**
     * dp->px
     * @param dpValue Float
     * @return Int
     */
    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        return (dpValue * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }


    /**
     * px->dp
     * @param pxValue Float
     * @return Int
     */
    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        return (pxValue / Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }


    /**
     *  px->sp
     * @param pxValue Float
     * @return Int
     */
    @JvmStatic
    fun px2sp(pxValue: Float): Int {
        return (pxValue / Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()
    }


    /**
     * bitmap->bytes
     * @param bitmap Bitmap
     * @param format CompressFormat
     * @return ByteArray
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap, format: Bitmap.CompressFormat): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * bytes -> bitmap
     *
     * @param bytes
     * bytes
     *
     * @return bitmap
     */
    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray): Bitmap? {
        return if (bytes.isEmpty()) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * drawable转bitmap
     */
    @JvmStatic
    fun drawableToBitamp(drawable: Drawable?): Bitmap? {
        drawable?.let {
            if (it is BitmapDrawable) {
                return it.bitmap
            }
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
            return bitmap
        } ?: let {
            return null
        }

    }
}