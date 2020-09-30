package com.android.jasper.framework.network.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.IOException

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
            response,
            listener
        )
        return response.newBuilder().body(responseBody).build()
    }

    private inner class ProgressResponseBody constructor(
        private val response: Response,
        private val listener: IProgressListener?
    ) : ResponseBody() {
        private val responseBody by lazy { response.body }
        private var bufferedSource: BufferedSource? = null

        //ResponseBody 内容长度，部分接口拿不到，会返回-1，此时会没有进度回调
        private var contentLength: Long

        init {
            contentLength = responseBody?.contentLength() ?: -1L
            if (contentLength == -1L) {
                contentLength = contentLengFromHeader(response)
            }
        }

        /**
         * 从响应头获取长度
         * @param response Response
         * @return Long
         */
        private fun contentLengFromHeader(response: Response): Long {
            var contentLength: Long = -1
            //响应头Content-Range格式 : bytes 100001-20000000/20000001
            //开始下载位置-结束下载位置/
            response.header("Content-Range")?.let { headerValue ->
                try {
                    //斜杠下标
                    val divideIndex = headerValue.indexOf("/")
                    val blankIndex = headerValue.indexOf(" ")
                    //100001-20000000
                    val fromToValue = headerValue.substring(blankIndex + 1, divideIndex)
                    val splitArray = fromToValue.split("-".toRegex()).toTypedArray()
                    val start = splitArray[0].toLong()
                    val end = splitArray[1].toLong()
                    //要下载的总长度
                    contentLength = end - start + 1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return contentLength
        }


        override fun contentType(): MediaType? {
            return this.responseBody?.contentType()
        }

        override fun contentLength(): Long {
            return contentLength
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
                    //当前读取字节总数
                    private var totalReadBytes = 0L
                    //上次回调的进度
                    private var lastProgress:Double = 0.0
                    //上次回调时间
                    private var lastTime = 0L
                    @Throws(IOException::class)
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val readBytes = super.read(sink, byteCount)
                        //-1 代表读取完毕
                        if (readBytes == -1L) {
                            if (contentLength == -1L) {
                                contentLength = totalReadBytes
                            }
                        } else {
                            //未读取完，则累加已读取的字节
                            totalReadBytes += readBytes
                        }
                        //当前进度 = 当前已读取的字节 / 总字节
                        val currentProgress = (totalReadBytes.toDouble() * 100.0 / contentLength)
                        //前进度大于上次进度，才会更新进度
                        if (currentProgress in (lastProgress + 1)..100.0) {
                            val currentTime = System.currentTimeMillis()
                            //两次回调时间大于45毫秒才更新进度
                            if (currentTime - lastTime >30L){
                                lastTime = currentTime
                                lastProgress = currentProgress
                                listener?.onProgress(
                                    lastProgress,
                                    totalReadBytes,
                                    contentLength
                                )
                            }
                        }
                        return readBytes
                    }
                }.buffer()
            }
        }
    }

    interface IProgressListener {
        /**
         *
         * @param progress Double 0..100.0
         * @param totalReadBytes Long  当前下载已经读取的总字节(不包含断点下载\上传 之前已经读取的字节长度)
         * @param contentLength Long 文件的大小
         */
        fun onProgress( progress:Double, totalReadBytes:Long, contentLength:Long)
    }
}