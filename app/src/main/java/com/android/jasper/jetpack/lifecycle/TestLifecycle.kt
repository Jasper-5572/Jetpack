package com.android.jasper.jetpack.lifecycle

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.android.jasper.framework.util.LogUtils

/**
 *@author   Jasper
 *@create   2020/4/10 16:14
 *@describe
 *@update
 */
class TestLifecycle constructor(private val activity: Activity) : LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onCreate()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onStart()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onResume()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onPause()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onStop()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onDestroy()")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny() {
        LogUtils.i(activity.javaClass,"TestLifecycle:onAny()")
    }
}