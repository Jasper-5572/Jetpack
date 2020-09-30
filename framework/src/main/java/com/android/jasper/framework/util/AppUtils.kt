@file:Suppress("CheckedExceptionsKotlin", "DEPRECATION")

package com.android.jasper.framework.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Process
import android.view.View
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.nio.charset.Charset

/**
 *@author   Jasper
 *@create   2020/5/21 15:33
 *@describe
 *@update
 */
object AppUtils {
    /**
     * 检测当前APP是否在主进程
     * @param context Context
     * @return Boolean
     */
    @JvmStatic
    fun checkMainProcess(context: Context): Boolean {
        return getApplicationId(context).equals(getCurrentProcessName(),true)
    }

    @JvmStatic
    fun getApplicationId(context: Context): String {
        return context.packageName
    }

    /**
     * 获取当前线程名
     * @return String
     */
    @JvmStatic
    fun getCurrentProcessName(): String {
        FileInputStream("/proc/self/cmdline").use { fileInputStream ->
            val buffer = ByteArray(256)
            var len = 0
            var b: Int
            while (fileInputStream.read().also { b = it } > 0 && len < buffer.size) {
                buffer[len++] = b.toByte()
            }
            if (len > 0) {
                return String(buffer, 0, len, Charset.forName("UTF-8"))
            }
        }

        return ""

    }

    /**
     * 检测当前App是否运行在后台
     *
     * @param context context
     * @return 是否运行在后台
     */
    @JvmStatic
    fun checkAppRunInBackground(context: Context): Boolean {
        val sdkVersion = Build.VERSION.SDK_INT
        val systemService = context.getSystemService(Context.ACTIVITY_SERVICE)
        val runningActivityPackageName = try {
            when {
                //获取系统api版本号,如果是5x系统就用这个方法获取当前运行的包名
                sdkVersion >= Build.VERSION_CODES.LOLLIPOP -> getCurrentPkgName(context)
                systemService is ActivityManager -> systemService.getRunningTasks(1)[0].topActivity?.packageName
                else -> ""
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            ""
        }
        return runningActivityPackageName == context.packageName
    }

    /**
     * 获取应用程序名称
     *
     * @param context context
     * @return 应用程序名称
     */
    @JvmStatic
    fun getAppName(context: Context): String {
        return try {
            context.resources.getString(
                PackageUtils.getPackageInfo(context)?.applicationInfo?.labelRes
                    ?: View.NO_ID
            )
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @param context context
     * @return 当前应用的版本名称
     */
    @JvmStatic
    fun getVersionName(context: Context): String {
        return PackageUtils.getPackageInfo(context)?.versionName.toString()
    }

    /**
     * 获取应用程序版本号[context]context
     */
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getVersionCode(context: Context): Int {
        return PackageUtils.getPackageInfo(context)?.versionCode ?: 0
    }


    /**
     * 5x系统以后利用反射获取当前栈顶activity的包名
     *
     * @param context context
     * @return 包名
     */
    @JvmStatic
    private fun getCurrentPkgName(context: Context): String {
        val startTaskToFront = 2
        val field = try {
            ActivityManager.RunningAppProcessInfo::class.java.getDeclaredField("processState")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val systemService = context.getSystemService(Context.ACTIVITY_SERVICE)
        if (systemService is ActivityManager) {
            val appList = systemService.runningAppProcesses
            for (runAppProcessInfo in appList) {
                if (runAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if ((field?.getInt(runAppProcessInfo)) == startTaskToFront) {
                        return runAppProcessInfo.processName
                    }
                }
            }
        }
        return ""
    }

    /**
     * 安装apk
     * @param context Context
     * @param apkPath String apk路径
     */
    @JvmStatic
    fun installApk(context: Context, apkPath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val file = File(apkPath)
        val apkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(context, context.packageName + ".frameworkFileProvider", file)
        } else {
            Uri.fromFile(file)
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}