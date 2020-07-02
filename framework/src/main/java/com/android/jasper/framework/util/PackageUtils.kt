package com.android.jasper.framework.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 *@author   Jasper
 *@create   2020/5/21 15:33
 *@describe package相关方法
 *@update
 */
object PackageUtils {
    /**
     * 获取当前应用的PackageInfo
     * @param context Context
     * @return PackageInfo?
     */
    @JvmStatic
    fun getPackageInfo(context: Context): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}