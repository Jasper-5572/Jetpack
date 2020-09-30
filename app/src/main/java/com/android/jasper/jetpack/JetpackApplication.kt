package com.android.jasper.jetpack

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.android.jasper.framework.JasperConfiguration
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.framework.web.WebViewPool
import com.android.jasper.jetpack.aidl.MainBinderPool
import com.android.jasper.jetpack.page.web.WebBinder
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 *@author   Jasper
 *@create   2020/5/21 16:47
 *@describe
 *@update
 */
class JetpackApplication : Application(), Thread.UncaughtExceptionHandler {
    override fun onCreate() {
        super.onCreate()
//        Thread.setDefaultUncaughtExceptionHandler(this)
        JasperFramework.INSTANCE.initialize(this)
        //设置全局的Header构建器  The flexible, easy to use, all in one drawer library for your Android project. Now brand new with material 2 design
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
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (JasperFramework.appDebug) {
            // 打印日志
            ARouter.openLog()
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this)
        LogUtils.i("JetpackApplication:ProcessName->${AppUtils.getCurrentProcessName()}")
        WebViewPool.INSTANCE.initWeb()
        //在主进程添加所有子进程用到的binder
        if (AppUtils.checkMainProcess(this)) {
            MainBinderPool.getInstance(this@JetpackApplication)
                .addBinderClass(1, WebBinder::class.java)
        } else {
            //如果是子进程 连接主进程的服务
            JasperFramework.safeLaunch {
                withContext(Dispatchers.IO) {
                    MainBinderPool.getInstance(this@JetpackApplication).connectService()
                }
            }

        }
//        if (currentProcessName.equals(AppUtils.getApplicationId(this) + ":web", true)) {
//        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        android.os.Process.killProcess(android.os.Process.myPid())
    }


}