package com.android.jasper.framework.util

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 *@author   Jasper
 *@create   2020/7/9 15:18
 *@describe
 *@update
 */
object DeviceUtils {
    /**
     * 小米
     */
    const val ROM_MIUI = "MIUI"
    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"

    /**
     *华为
     */
    const val ROM_EMUI = "EMUI"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"

    /**
     * OPPO手机
     */
    const val ROM_OPPO = "OPPO"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"

    const val ROM_SMARTISAN = "SMARTISAN"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"


    /**
     * vivo手机
     */
    const val ROM_VIVO = "VIVO"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"
    private var romVersion: String = ""
    private var romName: String = ""

    /**
     * 魅族手机
     */
    const val ROM_FLYME = "FLYME"





    /**
     * 360手机
     */
    const val ROM_QIKU = "QIKU"


    @SuppressLint("HardwareIds")
    @Suppress("DEPRECATION")
    private fun getDeviceInfo(): String? {
        val sb = StringBuffer()
        sb.append("主板： " + Build.BOARD)
        sb.append("\n系统启动程序版本号： ${Build.BOOTLOADER}")
        sb.append("\n系统定制商： ${Build.BRAND}")
        sb.append("\ncpu指令集： ${Build.CPU_ABI}")
        sb.append("\ncpu指令集2 ${Build.CPU_ABI2}")
        sb.append("\n设置参数： ${Build.DEVICE}")
        sb.append("\n显示屏参数：${Build.DISPLAY}")
        sb.append("\n无线电固件版本：${Build.getRadioVersion()}")
        sb.append("\n硬件识别码： ${Build.FINGERPRINT}")
        sb.append("\n硬件名称： ${Build.HARDWARE}")
        sb.append("\nHOST: ${Build.HOST}")
        sb.append("\nBuild.ID);${Build.ID}")
        sb.append("\n硬件制造商： ${Build.MANUFACTURER}")
        sb.append("\n版本： ${Build.MODEL}")
        sb.append("\n硬件序列号： ${Build.SERIAL}")
        sb.append("\n手机制造商： ${Build.PRODUCT}")
        sb.append("\n描述Build的标签： ${Build.TAGS}")
        sb.append("\nTIME: ${Build.TIME}")
        sb.append("\nbuilder类型${Build.TYPE}")
        sb.append("\nUSER: ${Build.USER}")
        return sb.toString()
    }

    //华为
    fun isEmui(): Boolean {
        return check(ROM_EMUI)
    }

    //小米
    fun isMiui(): Boolean {
        return check(ROM_MIUI)
    }

    //vivo
    fun isVivo(): Boolean {
        return check(ROM_VIVO)
    }

    //oppo
    fun isOppo(): Boolean {
        return check(ROM_OPPO)
    }

    //魅族
    fun isFlyme(): Boolean {
        return check(ROM_FLYME)
    }

    //360手机
    fun is360(): Boolean {
        return check(ROM_QIKU) || check("360")
    }

    fun isSmartisan(): Boolean {
        return check(ROM_SMARTISAN)
    }

    fun getName(): String {
        if (TextUtils.isEmpty(romName)) {
            check("")
        }
        return romName
    }

    fun getVersion(): String {
        if (TextUtils.isEmpty(romVersion)) {
            check("")
        }
        return romVersion
    }


    fun check(rom: String?): Boolean {
        if (!TextUtils.isEmpty(romVersion)) {
            return romVersion.equals(rom, false)
        }
        if (!TextUtils.isEmpty(getProp(KEY_VERSION_MIUI).also { romVersion = it })) {
            romName = ROM_MIUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_EMUI).also { romVersion = it })) {
            romName = ROM_EMUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_OPPO).also { romVersion = it })) {
            romName = ROM_OPPO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_VIVO).also { romVersion = it })) {
            romName = ROM_VIVO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_SMARTISAN).also { romVersion = it })) {
            romName = ROM_SMARTISAN
        } else {
            romVersion = Build.DISPLAY
            if (romVersion.toUpperCase(Locale.ROOT).contains(ROM_FLYME)) {
                romName = ROM_FLYME
            } else {
                romVersion = Build.UNKNOWN
                romName = Build.MANUFACTURER.toUpperCase(Locale.ROOT)
            }
        }
        return romName.equals(rom, false)
    }

    /**
     *
     * @param name String
     * @return String?
     */
    fun getProp(name: String): String {
        var result = ""
        Runtime.getRuntime().exec("getprop $name")?.let { process ->
            BufferedReader(InputStreamReader(process.inputStream), 1024).use { bufferedReader ->
                result = bufferedReader.readLine()
            }
        }
        return result

    }


}