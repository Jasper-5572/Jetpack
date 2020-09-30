package com.android.jasper.framework.cache

import android.content.Context
import java.io.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

/**
 *@author   Jasper
 *@create   2020/8/3 18:03
 *@describe
 *@update
 */
internal class InternalCache constructor(context: Context, cacheName: String = "jasperCache", private val cacheMaxSize: Long = 10 * 1024 * 1024) :
    ICache {
    /**
     * 统计缓存大小
     */
    private val cacheSizeAtomic: AtomicLong by lazy { AtomicLong() }

    /**
     * 记录缓存的文件
     */
    private val fileList by lazy { CopyOnWriteArrayList<File>() }

    /**
     * 缓存文件夹
     */
    private val cacheDir by lazy { File(context.cacheDir, cacheName).apply { if (!exists()) mkdir() } }

    init {
        var totalFileSize = 0L
        //统计缓存文件夹里面所有的文件及缓存大小
        cacheDir.listFiles()?.forEach { file ->
            totalFileSize += file.length()
            fileList.add(file)
        }
        cacheSizeAtomic.set(totalFileSize)
    }


    /**
     * 根据文件时间 移除
     * @return Long
     */
    private fun removeLastModifiedFile(): Long {
        return if (fileList.isEmpty()) {
            0L
        } else {
            //最早使用的文件
            val minLastModifiedFile = Collections.min(fileList) { o1, o2 ->
                o1.lastModified().compareTo(o2.lastModified())
            }
            val fileSize = minLastModifiedFile.length()
            minLastModifiedFile.delete()
            fileList.remove(minLastModifiedFile)
            fileSize
        }
    }

    override fun save(key: String, value: String, aliveTimeSecond: Long) {
        File(cacheDir, key.hashCode().toString() + "").let { file ->
            BufferedWriter(FileWriter(file), 1024).use {
                it.write(ICache.addDateInfo(value, aliveTimeSecond))
            }
            val fileSize = file.length()
            //如果当前缓存大小+此次缓存文件的大小 超过最大缓存 则根据时间移除之前的文件
            var currentCacheSize = cacheSizeAtomic.get()
            while (currentCacheSize + fileSize > cacheMaxSize) {
                currentCacheSize = cacheSizeAtomic.addAndGet(-removeLastModifiedFile())
            }
            //如果缓存不包含此文件 则表示新增的 需要统计到总文件大小
            if (!fileList.contains(file)){
                cacheSizeAtomic.addAndGet(fileSize)
                file.setLastModified(System.currentTimeMillis())
                fileList.add(file)
            }

        }
    }

    override fun get(key: String, defaultValue: String): String {
        File(cacheDir, key.hashCode().toString() + "").let { file ->
            if (!file.exists()) {
                return defaultValue
            } else {
                val stringBuilder = StringBuilder()
                BufferedInputStream(FileInputStream(file)).use { bufferedInputStream ->
                    val buffer = ByteArray(8 * 1024)
                    var flag: Int
                    while (bufferedInputStream.read(buffer).also { flag = it } != -1) {
                        stringBuilder.append(String(buffer, 0, flag))
                    }
                }
                //如果已经过期 则移除
                if (ICache.isExpired(stringBuilder.toString())) {
                    fileList.remove(file)
                    file.delete()
                    return defaultValue
                }
                //返回没有时间信息前缀的原始数据
                return ICache.clearDateInfo(stringBuilder.toString())

            }
        }
    }

    override fun cacheSize(): Long = cacheSizeAtomic.get()

    override fun remove(key: String) {
        File(cacheDir, key.hashCode().toString() + "").let { file ->
            if (file.exists()) {
                cacheSizeAtomic.addAndGet(-file.length())
                file.delete()
                fileList.remove(file)
            }
        }
    }

    override fun clear() {
        fileList.forEach {
            it.delete()
        }
        cacheSizeAtomic.set(0L)
        fileList.clear()
    }

}