package com.android.jasper.framework.util

import android.text.TextUtils
import kotlin.experimental.and

/**
 *@author   Jasper
 *@create   2020/4/10 14:00
 *@describe
 *@update
 */
object StringUtils {
    /**
     * 拼接字符串
     *
     * @param s 目标字符串数组
     * @return 拼接后结果字符串
     */
    @JvmStatic
    fun buildString(vararg s: String?): String {
        var result = ""
        val stringBuilder = StringBuilder()
        s.forEach {
            stringBuilder.append(it).append(",")
        }
        val length = stringBuilder.length
        if (length > 0) {
            result = stringBuilder.substring(0, length - 1)
        }
        return result
    }

    /**
     * 字符串首字母转成大写
     *
     * @param s 需要转换的字符串
     * @return 转换后的字符串
     */
    @JvmStatic
    fun upperFirstLetter(s: String): String? {
        return if (TextUtils.isEmpty(s) || !Character.isLowerCase(s[0])) {
            s
        } else {
            ((s[0].toInt() - 32).toChar()).toString()+s.substring(1)
        }
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    @JvmStatic
    fun byteArrayToHexString(data: ByteArray?): String? {
        val result: String?
        result = if (data == null) {
            null
        } else {
            val stringBuilder = StringBuilder(data.size * 2)
            for (b in data) {
                val v = b and 0xff.toByte()
                if (v < 16) {
                    stringBuilder.append('0')
                }
                stringBuilder.append(Integer.toHexString(v.toInt()))
            }
            stringBuilder.toString()
        }
        return result
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    @JvmStatic
    fun hexStringToByteArray(s: String): ByteArray? {
        val result: ByteArray?
        if (TextUtils.isEmpty(s)) {
            result = null
        } else {
            val len = s.length
            result = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
                result[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character
                        .digit(s[i + 1], 16)).toByte()
                i += 2
            }
        }
        return result
    }

}