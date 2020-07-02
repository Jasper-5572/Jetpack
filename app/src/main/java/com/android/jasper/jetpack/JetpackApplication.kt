package com.android.jasper.jetpack

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.android.jasper.framework.JasperFramework
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.header.FalsifyHeader
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 *@author   Jasper
 *@create   2020/5/21 16:47
 *@describe
 *@update
 */
class JetpackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JasperFramework.INSTANCE.initialize(this)
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            //全局设置主题颜色
            layout.setPrimaryColorsId(R.color.colorMaster, android.R.color.white)
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context)
//            return@setDefaultRefreshHeaderCreator MaterialHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            //指定为经典Footer，默认是 BallPulseFooter
            return@setDefaultRefreshFooterCreator ClassicsFooter(context).setDrawableSize(30f)
//            return@setDefaultRefreshFooterCreator BallPulseFooter(context)
        }
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}