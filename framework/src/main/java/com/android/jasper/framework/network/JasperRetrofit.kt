package com.android.jasper.framework.network

import com.android.jasper.framework.network.json.DoubleTypeAdapter
import com.android.jasper.framework.network.json.IntTypeAdapter
import com.android.jasper.framework.network.json.LongTypeAdapter
import com.android.jasper.framework.network.json.StringTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

/**
 *@author   Jasper
 *@create   2020/7/8 14:26
 *@describe
 *@update
 */
internal class JasperRetrofit private constructor() {
    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
                .baseUrl(JasperNetwork.BASE_URL)
                .client(JasperOkHttp.INSTANCE.okHttpClient)
//            .addConverterFactory(FkJsonConverterFactory.create(buildGson()))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
    }

    companion object {
        val INSTANCE by lazy { JasperRetrofit() }
        /**
         * 增加后台返回""和"null"的处理
         * 1.int=>0
         * 2.double=>0.00
         * 3.long=>0L
         *
         * @return
         */
        private fun buildGson(): Gson {
            return GsonBuilder()
                    .registerTypeAdapter(Int::class.java, IntTypeAdapter())
                    .registerTypeAdapter(Int::class.javaPrimitiveType, IntTypeAdapter())
                    .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
                    .registerTypeAdapter(Double::class.javaPrimitiveType, DoubleTypeAdapter())
                    .registerTypeAdapter(Long::class.java, LongTypeAdapter())
                    .registerTypeAdapter(Long::class.javaPrimitiveType, LongTypeAdapter())
                    .registerTypeAdapter(String::class.java, StringTypeAdapter())
                    .create()
        }
    }
}

