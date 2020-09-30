package com.android.jasper.framework

import android.content.Context
import android.content.res.Resources.NotFoundException
import com.android.jasper.framework.adapter.recycler_view.AdapterLayoutResBean
import com.android.jasper.framework.network.UrlAdapter
import com.android.jasper.framework.adapter.recycler_view.BaseAdapter
import okhttp3.OkHttpClient
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
    var jasperConfiguration: JasperConfiguration? = null
        private set

    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JasperConfigurationManager() }
    }

    internal fun initialize(context: Context, jasperConfiguration: JasperConfiguration?) {
        constantsConfigSet.clear()

        //如果动态添加了相关配置
        this.jasperConfiguration = jasperConfiguration
        jasperConfiguration?.constantsConfigSet?.let {
            constantsConfigSet.addAll(it)
        }
        val xmlName = jasperConfiguration?.xmlConfigurationName ?: "jasper_framework_configuration"
        //解析xml配置
        val xmlConstantsSet = JasperXmlConfiguration().parseXml(context, xmlName)
        constantsConfigSet.addAll(xmlConstantsSet)
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

data class JasperConfiguration @JvmOverloads constructor(
    /**
     * 常量配置 优先代码配置 然后xml配置
     *
     */
    val constantsConfigSet: Set<ConstantsConfigBean>? = null,

    /**
     * 添加okhttp配置
     */
    val addOkHttpConfig: (okhttp: OkHttpClient.Builder) -> Unit = { },

    /**
     * 地址是配置
     */
    val urlAdapter: UrlAdapter? = null,

    /**
     * 默认为jasper_framework_configuration
     */
    val xmlConfigurationName: String = "jasper_framework_configuration",
    /**
     * recyclerView的通用adapter[BaseAdapter]
     */
    val adapterLayoutRes: AdapterLayoutResBean = AdapterLayoutResBean()
)

/**
 *
 * @property key String?
 * @property value String?
 * @property description String?
 * @constructor
 */
data class ConstantsConfigBean(
    val key: String?,
    val value: String?,
    val description: String? = ""
) {
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
    fun parseXml(context: Context, xmlName: String): Set<ConstantsConfigBean> {
        val configurationSet = hashSetOf<ConstantsConfigBean>()
        val resourceId = context.resources.getIdentifier(
            xmlName,
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

