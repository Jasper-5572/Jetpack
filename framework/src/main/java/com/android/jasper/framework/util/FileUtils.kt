package com.android.jasper.framework.util

import android.os.Environment
import android.text.TextUtils
import com.android.jasper.framework.JasperFramework
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.*

/**
 *@author   Jasper
 *@create   2020/8/10 10:51
 *@describe
 *@update
 */
object FileUtils {

    fun write(responseBody: ResponseBody, savePath: String) {
        val saveFile = File(savePath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val buffer = ByteArray(1024)
        var len: Int
        FileOutputStream(saveFile).use { fos ->
            BufferedInputStream(responseBody.byteStream()).use { bis ->
                while (bis.read(buffer).also { len = it } != -1) {
                    fos.write(buffer, 0, len)
                    fos.flush()
                }
            }
        }
    }

    /**
     * 根据url获取文件保存路径
     * @param url String
     * @return String
     */
    fun getFileSavePath(url: String): String {
        val context = JasperFramework.INSTANCE.application
        val fileDir = context.getExternalFilesDir(null)?.let {
            File(it, "jasperDownload").apply {
            }
        } ?: File(context.cacheDir, "jasperDownload").apply {

        }
        fileDir.apply {
            if (!exists()){
                mkdir()
            }
        }//25377775
        return File(fileDir, getFileName(url)).path
    }

    /**
     * 根据url获取文件名称
     *
     * @param url 文件url
     * @return 文件名称
     */
    fun getFileName(url: String): String {
        return MD5Utils.getMD5(url)+url.substring(url.lastIndexOf("."),url.length)
    }
}