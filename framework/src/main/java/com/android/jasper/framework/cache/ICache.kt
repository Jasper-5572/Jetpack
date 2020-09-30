package com.android.jasper.framework.cache

import com.android.jasper.framework.expansion.parseLong

/**
 *@author   Jasper
 *@create   2020/8/4 11:31
 *@describe
 *@update
 */
interface ICache {
    companion object {
        const val SEPARATOR = '-'

        /**
         * 根据存活时间创建时间信息前缀  currentTime_aliveTimeSecond-
         * 如果aliveTimeSecond<=0表示不添加存活时间
         * @param aliveTimeSecond Int
         * @return String
         */
        private fun createDateInfo(aliveTimeSecond: Long): String {
            return if (aliveTimeSecond > 0) {
                var currentTime = System.currentTimeMillis().toString()
                while (currentTime.length < 13) {
                    currentTime = "0$currentTime"
                }
                "${currentTime}_${aliveTimeSecond}$SEPARATOR"
            } else {
                ""
            }

        }

        /**
         * 添加存活相关信息
         * @param value String
         * @param aliveTimeSecond Long <=0 表示不添加
         * @return String
         */
        fun addDateInfo(value: String, aliveTimeSecond: Long) = value + createDateInfo(aliveTimeSecond)

        /**
         * 判断是否有时间前缀 如果_下划线的位置出现在第14位（前面13位为时间戳）且包含分割线[SEPARATOR]
         * @param data ByteArray
         * @return Boolean
         */
        fun hasDateInfo(data: ByteArray): Boolean = data.size > 15 && data[13] == '_'.toByte() && getSeparatorIndexOf(data) > 14

        /**
         * 获取分隔符的位置（第一个出现分隔符的位置）
         * @param dataArray ByteArray
         * @return Int
         */
        private fun getSeparatorIndexOf(dataArray: ByteArray): Int {
            for (i in dataArray.indices) {
                if (dataArray[i] == SEPARATOR.toByte()) {
                    return i
                }
            }
            return -1
        }

        /**
         * 清除时间信息前缀
         * @param dataString String
         * @return String
         */
        fun clearDateInfo(dataString: String): String {
            return if (hasDateInfo(dataString.toByteArray())) {
                dataString.substring(getSeparatorIndexOf(dataString.toByteArray()) + 1, dataString.length)
            } else {
                dataString
            }
        }

        /**
         * 清除时间信息前缀
         * @param dataArray ByteArray
         * @return ByteArray
         */
        fun clearDateInfo(dataArray: ByteArray): ByteArray {
            return if (hasDateInfo(dataArray)) {
                copyArrayRange(dataArray, getSeparatorIndexOf(dataArray) + 1, dataArray.size)
            } else {
                dataArray
            }
        }


        /**
         * 判断当前是否过期
         * @param dataString String
         * @return Boolean true 过期
         */
        fun isExpired(dataString: String): Boolean = isExpired(dataString.toByteArray())

        /**
         * 判断当前是否过期
         * @param dataArray ByteArray
         * @return Boolean  true 过期
         */
        fun isExpired(dataArray: ByteArray): Boolean {
            getDateInfo(dataArray)?.let { dateInfoArray ->
                if (dateInfoArray.size == 2) {
                    var savedTimestampString: String
                    savedTimestampString = dateInfoArray[0]
                    while (savedTimestampString.startsWith("0")) {
                        savedTimestampString = savedTimestampString.substring(1, savedTimestampString.length)
                    }
                    val savedTimestamp = savedTimestampString.parseLong()
                    val aliveTimeSecond = dateInfoArray[1].parseLong()
                    //当前时间>（保存时间+存活时间） 则表示过期了
                    return System.currentTimeMillis() > savedTimestamp + aliveTimeSecond * 1000L
                }
            }
            return false
        }

        private fun getDateInfo(dataArray: ByteArray): Array<String>? {
            return if (hasDateInfo(dataArray)) {
                //保存的时间戳
                val savedTimestamp = String(copyArrayRange(dataArray, 0, 13))
                //存活时间（单位S）
                val aliveTimeSecond = String(copyArrayRange(dataArray, 14, getSeparatorIndexOf(dataArray)))
                arrayOf(savedTimestamp, aliveTimeSecond)
            } else {
                null
            }
        }

        /**
         * 根据指定位置[fromIndex]-[toIndex]从[dataArray]copy数据
         * @param dataArray ByteArray
         * @param fromIndex Int
         * @param toIndex Int
         * @return ByteArray
         */
        private fun copyArrayRange(dataArray: ByteArray, fromIndex: Int, toIndex: Int): ByteArray {
            val newLength = toIndex - fromIndex
            return if (newLength < 0) {
                throw IllegalArgumentException("$fromIndex > $toIndex")
            } else {
                val copyArray = ByteArray(newLength)
                System.arraycopy(dataArray, fromIndex, copyArray, 0, (dataArray.size - fromIndex).coerceAtMost(newLength))
                copyArray
            }
        }


    }

    /**
     *
     * @param key String
     * @param value String
     * @param aliveTimeSecond Long 存活时间 <=0表示永久存活 单位秒
     */
    fun save(key: String, value: String, aliveTimeSecond: Long = -1)

    /**
     * 从缓存拿数据
     * @param key String
     * @param defaultValue String
     * @return String
     */
    fun get(key: String, defaultValue: String = ""): String


    fun cacheSize(): Long


    fun remove(key: String)

    fun clear()
}