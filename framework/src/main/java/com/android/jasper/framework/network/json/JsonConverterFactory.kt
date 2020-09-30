package com.android.jasper.framework.network.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 *@author   Jasper
 *@create   2020/7/8 13:40
 *@describe
 *@update
 */
class JsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {


    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
                val adapter = gson.getAdapter(TypeToken.get(type))
        @Suppress("UNCHECKED_CAST")
        return GsonRequestBodyConverter<Any>(gson, adapter as TypeAdapter<Any>?)
    }
}

class GsonRequestBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val typeAdapter: TypeAdapter<T>?
) : Converter<T, RequestBody> {
    companion object {
        private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
        private val UTF_8 = Charset.forName("UTF-8")
    }

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(
            buffer.outputStream(),
            UTF_8
        )
        gson.newJsonWriter(writer)?.use {
            typeAdapter?.write(it, value)
        }
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }


}

class GsonResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val typeAdapter: TypeAdapter<T>?
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): T? {
        val jsonReader = gson.newJsonReader(responseBody.charStream())
        return gson.newJsonReader(responseBody.charStream())?.apply {
            isLenient = true
        }?.use {
            return@use typeAdapter?.read(jsonReader)
        }
    }


}