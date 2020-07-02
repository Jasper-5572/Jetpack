package com.android.jasper.framework

import android.content.Context
import android.content.res.Resources.NotFoundException
import okhttp3.Interceptor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe 配置文件管理类
 *@update
 */

class JasperConfigurationManager {
    private val constantsConfigSet by lazy { hashSetOf<ConstantsConfigBean>() }
    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JasperConfigurationManager() }
    }

    internal fun initialize(context: Context, jasperConfiguration: JasperConfiguration?) {
        constantsConfigSet.clear()
        //解析xml配置
        val xmlConstantsSet = JasperXmlConfiguration().parseXml(context)
        constantsConfigSet.addAll(xmlConstantsSet)
        //如果动态添加了相关配置
        jasperConfiguration?.constantsConfigSet?.let {
            constantsConfigSet.addAll(it)
        }

    }
    /**
     * 根据对应的key获取配置的数据
     * @param key String
     * @return String
     */
    fun getValue(key: String): String {
        if (key.isEmpty()) {
            throw RuntimeException("key is empty")
        }
        constantsConfigSet.forEach {
            if (key == it.key) {
                return it.value ?: ""
            }
        }
        throw IllegalArgumentException("You don't add the $key in framework_configuration and ${JasperConfiguration::class.java.simpleName}")
    }
}

class JasperConfiguration {
    /**
     * 常量配置
     */
    var constantsConfigSet: Set<ConstantsConfigBean>? = null

    /**
     * 拦截器配置
     */
    var interceptorList: MutableList<Interceptor>? = null

}

/**
 *
 * @property key String?
 * @property value String?
 * @property description String?
 * @constructor
 */
data class ConstantsConfigBean(val key: String?, val value: String?, val description: String?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConstantsConfigBean) return false

        if (key != other.key) return false
        if (value != other.value) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}


private class JasperXmlConfiguration {
    /**
     * 解析配置文件
     * @param context Context
     * @throws NotFoundException
     */
     fun parseXml(context: Context): Set<ConstantsConfigBean> {
        val configurationSet = hashSetOf<ConstantsConfigBean>()
        val resourceId = context.resources.getIdentifier(
            "jasper_framework_configuration",
            "xml",
            context.packageName
        )
        val xmlParser = context.resources.getXml(resourceId)
        try {
            var eventType = xmlParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "item" == xmlParser.name) {
                    configurationSet.add(
                        ConstantsConfigBean(
                            key = xmlParser.getAttributeValue(null, "key"),
                            value = xmlParser.getAttributeValue(null, "value"),
                            description = xmlParser.getAttributeValue(null, "description")
                        )
                    )
                }
                eventType = xmlParser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return configurationSet
    }


}

