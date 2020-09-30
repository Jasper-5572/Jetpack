package com.android.jasper.framework.network.json

import com.google.gson.*
import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Type


/**
 *@author   Jasper
 *@create   2020/7/8 13:40
 *@describe
 *@update
 */
/**
 * 定义为long类型,如果后台返回""或者null,则返回0
 */
class LongTypeAdapter : JsonSerializer<Long?>, JsonDeserializer<Long?> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        try {
            if (json.asString == "" || json.asString == "null") {
                return 0L
            }
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
        return try {
            json.asLong
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0L
        }
    }

    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}

/**
 * 定义为int类型,如果后台返回""或者null,则返回0
 */
class IntTypeAdapter : JsonSerializer<Int?>, JsonDeserializer<Int> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Int {
        try {
            if (json.asString == "" || json.asString == "null") {
                return 0
            }
        } catch (ignore: java.lang.Exception) {
            ignore.printStackTrace()
        }
        return try {
            json.asInt
        } catch (e: java.lang.NumberFormatException) {
            e.printStackTrace()
            0
        }
    }

    override fun serialize(src: Int?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src)
    }
}

/**
 * 定义为double类型,如果后台返回""或者null,则返回0.00
 */
class DoubleTypeAdapter : JsonSerializer<Double?>, JsonDeserializer<Double> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Double {
        try {
            if (json.asString == "" || json.asString == "null") {
                return 0.00
            }
        } catch (ignore: java.lang.Exception) {
            ignore.printStackTrace()
        }
        return try {
            json.asDouble
        } catch (e: java.lang.NumberFormatException) {
            e.printStackTrace()
            0.0

        }
    }

    override fun serialize(src: Double?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src)
    }
}


class StringTypeAdapter : TypeAdapter<String?>() {
    override fun read(reader: JsonReader): String {
        val jsonToken:JsonToken=reader.peek()
        return if (jsonToken=== JsonToken.NULL){
            reader.nextNull()
            ""
        }else if (jsonToken==JsonToken.BOOLEAN){
            reader.nextBoolean().toString()
        }else{
            val jsonStr= reader.nextString()
            if (jsonStr == "null") {
                ""
            } else {
                jsonStr
            }
        }


    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: String?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.value(value)
    }
}