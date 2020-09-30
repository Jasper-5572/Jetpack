package com.android.jasper.framework.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 *@author   Jasper
 *@create   2020/8/4 14:38
 *@describe
 *@update
 */
object MD5Utils {


    fun getMD5(dataString: String): String = getMD5(dataString.toByteArray())

    /**
     * MD5 16进制
     * @param bytes ByteArray
     * @return String
     */
    fun getMD5(bytes: ByteArray): String {
        return try {
            MessageDigest.getInstance("MD5").let { md5Digest ->
                md5Digest.reset()
                toHexString(md5Digest.digest(bytes))
            }
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
    }

    /**
     * hex值
     * @param dataArray ByteArray
     * @return String
     */
    fun toHexString(dataArray: ByteArray): String {
        val result = StringBuilder()
        dataArray.forEach { byte ->
            val hexString = Integer.toHexString((byte.toInt() and 0xFF))
            if (hexString.length == 1)
                result.append("0").append(hexString)
            else
                result.append(hexString)
        }
        return result.toString()
    }

}