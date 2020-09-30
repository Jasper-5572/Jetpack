package com.android.jasper.jetpack.page.web

import android.app.Service
import android.content.Context
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.live_data.EventMessage
import com.android.jasper.framework.live_data.LiveDataBus
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.IWebAidlInterface
import com.android.jasper.jetpack.aidl.MainBinderPool

/**
 *@author   Jasper
 *@create   2020/8/6 13:59
 *@describe
 *@update
 */

class WebBinder : IWebAidlInterface.Stub() {
    companion object {
        const val BINDER_KEY = 1
        fun get(context: Context):IWebAidlInterface?{
          return  asInterface(MainBinderPool.getInstance(context).queryBinder(BINDER_KEY))
        }
    }

    override fun gotoLogin() {
        //thread=Binder:6381_2
//        LogUtils.i("IWebAidlInterface:gotoLogin()")
        LogUtils.i("binder:gotoLogin()->thread=${Thread.currentThread().name},progress=${AppUtils.getCurrentProcessName()}")

//        LiveDataBus.INSTANCE.sendMessage(EventMessage("drawer_layout", true), true)
        JasperFramework.safeLaunch {
            LiveDataBus.INSTANCE.sendMessage(EventMessage("drawer_layout", true), true)
        }
    }

    override fun getLoginToken(): String {
        return ""
    }

}