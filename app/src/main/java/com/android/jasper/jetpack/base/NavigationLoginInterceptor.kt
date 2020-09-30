package com.android.jasper.jetpack.base

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.android.jasper.base.ARounterPath
import com.android.jasper.framework.util.LogUtils

/**
 *@author   Jasper
 *@create   2020/7/7 14:12
 *@describe
 *@update
 */
@Interceptor(name = "login", priority = 6)
class NavigationLoginInterceptor : IInterceptor {
    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
        postcard?.let {
            LogUtils.i("NavigationLoginInterceptor->process(postcard:$postcard)")
            when (it.path) {
                ARounterPath.USER_LIST -> {
                    callback?.onInterrupt(null)
                }
                else -> callback?.onContinue(postcard)
            }
        }

    }

    override fun init(context: Context?) {
        LogUtils.i("NavigationLoginInterceptor->init(context:$context)")
        context?.let {
        }
    }
}