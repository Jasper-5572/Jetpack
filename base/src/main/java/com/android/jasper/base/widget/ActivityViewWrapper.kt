package com.android.jasper.base.widget

import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 *@author   Jasper
 *@create   2020/7/9 11:28
 *@describe
 *@update
 */
interface ActivityViewWrapper : DefaultLifecycleObserver {
    @LayoutRes
    fun getLayoutResource(): Int


    @MainThread
    fun initView(activity: AppCompatActivity)
}