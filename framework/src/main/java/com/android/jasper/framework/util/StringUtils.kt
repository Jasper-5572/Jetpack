package com.android.jasper.framework.util

import android.text.TextUtils
import okhttp3.internal.indexOfFirstNonAsciiWhitespace
import java.io.UnsupportedEncodingException
import kotlin.experimental.and

/**
 *@author   Jasper
 *@create   2020/4/10 14:00
 *@describe
 *@update
 */
object StringUtils {

    @JvmStatic
    fun isLink(s: String): Boolean {
        val pos = s.indexOfFirstNonAsciiWhitespace()
        val isHttpUrl = s.regionMatches(pos, "https:", 0, 6, ignoreCase = true)
        val isHttpsUrl = s.regionMatches(pos, "http:", 0, 5, ignoreCase = true)
        return isHttpUrl || isHttpsUrl
    }

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
            ((s[0].toInt() - 32).toChar()).toString() + s.substring(1)
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

    private val base64EncodeChars = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/')
    private val base64DecodeChars = byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53,
            54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
            -1, -1, -1)


    /**
     * 编码
     * @param data ByteArray
     * @return String
     */
    fun encode(data: ByteArray): String {
        val sb = StringBuffer()
        val len = data.size
        var i = 0
        var b1: Int
        var b2: Int
        var b3: Int
        while (i < len) {
            b1 = (data[i++] and 0xff.toByte()).toInt()
            if (i == len) {
                sb.append(base64EncodeChars[b1 ushr 2])
                sb.append(base64EncodeChars[b1 and 0x3 shl 4])
                sb.append("==")
                break
            }
            b2 = (data[i++] and 0xff.toByte()).toInt()
            if (i == len) {
                sb.append(base64EncodeChars[b1 ushr 2])
                sb.append(base64EncodeChars[b1 and 0x03 shl 4 or (b2 and 0xf0 ushr 4)])
                sb.append(base64EncodeChars[b2 and 0x0f shl 2])
                sb.append("=")
                break
            }
            b3 = (data[i++] and 0xff.toByte()).toInt()
            sb.append(base64EncodeChars[b1 ushr 2])
            sb.append(base64EncodeChars[b1 and 0x03 shl 4 or (b2 and 0xf0 ushr 4)])
            sb.append(base64EncodeChars[b2 and 0x0f shl 2 or (b3 and 0xc0 ushr 6)])
            sb.append(base64EncodeChars[b3 and 0x3f])
        }
        return sb.toString()
    }


    /**
     * 解码
     * @param str String
     * @return ByteArray
     */
    fun decode(str: String): ByteArray {
        try {
            return decodePrivate(str)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return byteArrayOf()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun decodePrivate(str: String): ByteArray {
        val sb = StringBuilder()
        val data: ByteArray = str.toByteArray(charset("US-ASCII"))
        val len = data.size
        var i = 0
        var b1: Int
        var b2: Int
        var b3: Int
        var b4: Int
        while (i < len) {
            do {
                b1 = base64DecodeChars[data[i++].toInt()].toInt()
            } while (i < len && b1 == -1)
            if (b1 == -1) {
                break
            }
            do {
                b2 = base64DecodeChars[data[i++].toInt()].toInt()
            } while (i < len && b2 == -1)
            if (b2 == -1) {
                break
            }
            sb.append((b1 shl 2 or (b2 and 0x30 ushr 4)).toChar())
            do {
                b3 = data[i++].toInt()
                if (b3 == 61) {
                    return sb.toString().toByteArray(charset("iso8859-1"))
                }
                b3 = base64DecodeChars[b3].toInt()
            } while (i < len && b3 == -1)
            if (b3 == -1) {
                break
            }
            sb.append((b2 and 0x0f shl 4 or (b3 and 0x3c ushr 2)).toChar())
            do {
                b4 = data[i++].toInt()
                if (b4 == 61) {
                    return sb.toString().toByteArray(charset("iso8859-1"))
                }
                b4 = base64DecodeChars[b4].toInt()
            } while (i < len && b4 == -1)
            if (b4 == -1) {
                break
            }
            sb.append((b3 and 0x03 shl 6 or b4).toChar())
        }
        return sb.toString().toByteArray(charset("iso8859-1"))
    }
}