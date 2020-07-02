package com.android.jasper.framework.network.interceptor

import com.android.jasper.framework.util.LogUtils
import okhttp3.*
import okio.Buffer
import java.io.EOFException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 *@author   Jasper
 *@create   2020/6/4 15:26
 *@describe log拦截器
 *@update
 */
class LoggerInterceptor : Interceptor {
    private val utf8 = Charset.forName("UTF-8")
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val connection = chain.connection()
        //打印请求
        logRequest(request, connection)
        val startNs = System.nanoTime()
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            LogUtils.i("<-- HTTP FAILED: $e")
            throw e
        }
        //打印响应
        logResponse(startNs, response)
        return response

    }

    /**
     * 打印Response
     * @param startNs Long
     * @param response Response
     */
    private fun logResponse(startNs: Long, response: Response) {
        val responseLogString: java.lang.StringBuilder = java.lang.StringBuilder()
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        responseLogString.append("<-- ")
            .append(response.code)
            .append(if (response.message.isEmpty()) "" else ' '.toString() + response.message)
            .append(" ${response.request.url}")
            .append(" (" + tookMs + "ms" + ("") + ')')
        response.headers.let { headers: Headers ->
            responseLogString.append("\n")
                .append("HEADERS:{ ")
            var i = 0
            val count = headers.size
            while (i < count) {
                responseLogString.append("${headers.name(i)}:${headers.value(i)}").append(" , ")
                i++
            }
            responseLogString.append(" }").append("\n")
        }
        if (bodyEncoded(response.headers)) {
            responseLogString.append(" END HTTP (encoded body omitted) ")
        } else {
            response.body?.let { responseBody: ResponseBody ->
                val contentLength = responseBody.contentLength()
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                val charset = responseBody.contentType()?.charset(utf8) ?: utf8
                if (!isPlaintext(buffer)) {
                    responseLogString.append("END HTTP (binary ${buffer.size}-byte body omitted)")
                    LogUtils.i(responseLogString.toString())
                    return
                } else if (contentLength != 0L) {
                    responseLogString
                        .append("JSON-RESULT: ")
                        .append(buffer.clone().readString(charset))
                        .append("\n")
                }
                responseLogString.append("END HTTP (binary ${buffer.size}-byte body)")
            }

        }
        LogUtils.i(responseLogString.toString())
    }

    /**
     * request的log
     * @param request Request
     * @param connection Connection?
     */
    private fun logRequest(request: Request, connection: Connection?) {
        val requestLogString: StringBuilder = StringBuilder().apply {
            append("\n")
            append("-->").append(request.method).append(" ").append(request.url)
            if (connection != null) {
                append(" ").append(connection.protocol())
            }

        }
        connection?.let {
            requestLogString.append(" ").append(it.protocol())
        }
        val requestBody: RequestBody? = request.body
        requestBody?.let {
            it.contentType()?.let { mediaType ->
                requestLogString.append("\n").append("Content-Type:$mediaType")
            }
            if (it.contentLength() != -1L) {
                requestLogString.append("\n").append("Content-Length:${it.contentLength()}")
            }
        }

        //head
        request.headers?.let { headers ->
            requestLogString.append("\n")
                .append("HEADERS:{ ")
            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                if (!"Content-Type".equals(
                        name,
                        ignoreCase = true
                    ) && !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    requestLogString.append("$name:${headers.value(i)}").append(" , ")
                }
                i++
            }
            requestLogString.append(" }").append("\n")
        }
        if (bodyEncoded(request.headers)) {
            requestLogString.append("END ${request.method} (encoded body omitted)")
        } else {
            val buffer = Buffer()
            requestBody?.writeTo(buffer)
            val charset = requestBody?.contentType()?.charset(utf8) ?: utf8

            requestLogString.append("")
            if (isPlaintext(buffer)) {
                requestLogString
                    .append("JSON-PARAMS: ")
                    .append(buffer.readString(charset))
                    .append("\n")
                    .append(
                        "END " + request.method
                                + " (" + requestBody?.contentLength() + "-byte body)"
                    )
            } else {
                requestLogString.append(
                    "END " + request.method + " (binary "
                            + requestBody?.contentLength() + "-byte body omitted)"
                )
            }
        }
        LogUtils.i(requestLogString.toString())
    }


    /**
     * 判断是否是纯文本
     * @param buffer Buffer
     * @return Boolean
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            e.printStackTrace()
            false
        }
    }


    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
}