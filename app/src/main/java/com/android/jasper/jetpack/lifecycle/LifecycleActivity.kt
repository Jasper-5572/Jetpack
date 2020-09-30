package com.android.jasper.jetpack.lifecycle

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.base.BaseActivity
import com.android.jasper.base.BaseViewModel

/**
 *@author   Jasper
 *@create   2020/4/10 14:00
 *@describe
 *@update
 */
class LifecycleActivity : BaseActivity<BaseViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.i(this.javaClass,"LifecycleActivity:onCreate()")
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(TestLifecycle(this))

    }

    override fun onStart() {
        super.onStart()
        LogUtils.i(this.javaClass,"LifecycleActivity:onStart()")
    }

    override fun onRestart() {
        super.onRestart()
        LogUtils.i(this.javaClass,"LifecycleActivity:onRestart()")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i(this.javaClass,"LifecycleActivity:onResume()")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.i(this.javaClass,"LifecycleActivity:onPause()")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.i(this.javaClass,"LifecycleActivity:onStop()")
    }
    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i(this.javaClass,"LifecycleActivity:onDestroy()")
    }

}