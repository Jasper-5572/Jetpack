package com.android.jasper.framework.network

import android.net.Uri
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.network.interceptor.LoggerInterceptor
import com.android.jasper.framework.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import kotlin.coroutines.coroutineContext

/**
 *@author   Jasper
 *@create   2020/7/8 16:25
 *@describe
 *@update
 */


class JasperDownload private constructor() {


    private val loadService by lazy {
        JasperRetrofit.INSTANCE.retrofitBuilder.client(
            JasperOkHttp.getOkHttpClientBuilder().addInterceptor(LoggerInterceptor()).build()
        ).baseUrl(JasperNetwork.BASE_URL)
            .build()
            .create(LoadService::class.java)
    }

    companion object {
        suspend fun download(
            url: String,
            savePath: String = FileUtils.getFileSavePath(url)
        ): Download {
            return INSTANCE.download(url, savePath)
        }

        fun downloadForever(
            url: String, savePath: String = FileUtils.getFileSavePath(url)
            , onProgress: DownalodProgress = { _, _, _ -> },
            onSuccess: DownloadSuccess = {},
            onError: DownloadError = {}
        ): Job {
            return JasperFramework.launcher {
                download(url, savePath)
                    .onError(onError)
                    .onProcess(onProgress)
                    .onSuccess(onSuccess)
                    .start()

            }.start()

        }

        private val INSTANCE by lazy { JasperDownload() }
    }

    private suspend fun download(
        url: String,
        savePath: String = FileUtils.getFileSavePath(url)
    ): Download {

        val downloadFile = File(savePath)
        val range = if (downloadFile.exists()) downloadFile.length() else 0
        return Download(loadService.downloadFile("bytes=$range-", url), downloadFile)
    }


}
typealias DownloadError = (Throwable) -> Unit
typealias DownalodProgress = (progress: Double, totalReadBytes: Long, contentLength: Long) -> Unit
typealias DownloadSuccess = (uri: Uri) -> Unit

class Download(val response: Response<ResponseBody>, val downloadFile: File) {
    /**
     * 错误
     */
    private var error: DownloadError = {}

    /**
     * 进度
     */
    private var process: DownalodProgress = { _, _, _ -> }

    /**
     * 下载完成
     */
    private var success: DownloadSuccess = {}
    fun onProcess(process: DownalodProgress): Download {
        this.process = process
        return this
    }

    fun onError(error: DownloadError): Download {
        this.error = error
        return this
    }

    fun onSuccess(success: DownloadSuccess): Download {
        this.success = success
        return this
    }

    @Suppress("EXPERIMENTAL_API_USAGE", "BlockingMethodInNonBlockingContext")
    suspend fun start() {
        flow {
            try {
                val responseBody = response.body() ?: throw RuntimeException("下载出错")
                //已经读取的进度
                val range = if (downloadFile.exists()) downloadFile.length() else 0
                val randomAccessFile = RandomAccessFile(downloadFile, "rw").apply {
                    if (range == 0L) {
                        setLength(responseBody.contentLength())
                    } else if (length() == responseBody.contentLength()) {

                    }
                    //光标位置
                    seek(range)
                }

                //写入文件
                val bufferSize = 1024 * 8

                var totalReadLength = range
                BufferedInputStream(
                    responseBody.byteStream(),
                    bufferSize
                ).use { bufferedInputStream ->
                    //文件总长度
                    val buffer = ByteArray(bufferSize)
                    var readLength: Int
                    while (bufferedInputStream.read(buffer, 0, bufferSize)
                            .also { readLength = it } != -1
                    ) {
                        randomAccessFile.write(buffer, 0, readLength)
                        totalReadLength += readLength
                        //下载进度
                        val currentProgress = totalReadLength / randomAccessFile.length().toDouble()
                        emit(

                            DownloadListener.Progress(
                                currentProgress, totalReadLength, randomAccessFile.length()
                            )
                        )
                    }
                }
                //下载完成
                emit(DownloadListener.Success(Uri.fromFile(downloadFile)))
            } catch (e: Exception) {
                //下载出错
                emit(DownloadListener.Error(e))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadListener.Error -> error(it.throwable)
                        is DownloadListener.Progress -> process(
                            it.progress,
                            it.totalReadBytes,
                            it.contentLength
                        )
                        is DownloadListener.Success -> success(it.uri)
                    }
                }
            }
    }
}


sealed class DownloadListener {
    /**
     * 成功回调
     * @property uri Uri
     * @constructor
     */
    class Success(val uri: Uri) : DownloadListener()


    /**
     * 进度
     * @constructor
     */
    class Progress(val progress: Double, val totalReadBytes: Long, val contentLength: Long) :
        DownloadListener()


    /**
     * 失败
     * @property throwable Throwable
     * @constructor
     */
    class Error(val throwable: Throwable) : DownloadListener()
}

private interface LoadService {


    /**
     *下载文件
     * @param range String range="bytes="+startPosition+"-"
     * @param url String 下载文件的url
     * @return Response<ResponseBody>
     */
    @Streaming
    @GET
    suspend fun downloadFile(
        @Header("Range") range: String,
        @Url url: String
    ): Response<ResponseBody>

}