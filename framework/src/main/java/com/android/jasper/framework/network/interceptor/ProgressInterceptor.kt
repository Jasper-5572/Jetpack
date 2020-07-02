package com.android.jasper.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*
import java.io.IOException
import java.lang.RuntimeException

/**
 *@author   Jasper
 *@create   2020/6/4 15:43
 *@describe 进度拦截器
 *@update
 */
class ProgressInterceptor constructor(private val listener: IProgressListener? = null) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBody = ProgressResponseBody(
            response.body,
            listener
        )
        return response.newBuilder().body(responseBody).build()
    }

    private inner class ProgressResponseBody constructor(
        private val responseBody: ResponseBody?,
        private val listener: IProgressListener?
    ) : ResponseBody() {
        init {
            listener?.onPreExecute(contentLength())
        }

        private var bufferedSource: BufferedSource? = null
        override fun contentType(): MediaType? {
            return this.responseBody?.contentType()
        }

        override fun contentLength(): Long {
            return this.responseBody?.contentLength() ?: 0L
        }

        @Throws(RuntimeException::class)
        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = createBufferedSource()
            }
            return bufferedSource ?: throw RuntimeException()
        }

        private fun createBufferedSource(): BufferedSource? {
            return responseBody?.let {
                object : ForwardingSource(it.source()) {
                    private var totalReadBytes = 0L

                    @Throws(IOException::class)
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val readBytes = super.read(sink, byteCount)
                        totalReadBytes += if (readBytes != -1L) readBytes else 0
                        listener?.onProgress(
                            totalReadBytes,
                            contentLength(),
                            readBytes,
                            readBytes == -1L
                        )
                        return readBytes
                    }
                }.buffer()
            }

        }

    }

    interface IProgressListener {

        /**
         * 在执行前
         * @param contentBytes Long 文件的大小
         */
        fun onPreExecute(contentBytes: Long)


        /**
         *
         * @param totalReadBytes Long 当前下载已经读取的总字节(不包含断点下载\上传 之前已经读取的字节长度)
         * @param contentBytes Long  文件的大小
         * @param readBytes Long 当前读取的字节
         * @param done Boolean 是否完成
         */
        fun onProgress(totalReadBytes: Long, contentBytes: Long, readBytes: Long, done: Boolean)
    }
}