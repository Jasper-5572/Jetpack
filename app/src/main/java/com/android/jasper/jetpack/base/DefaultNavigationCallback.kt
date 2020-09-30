package com.android.jasper.jetpack.base

import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback

/**
 *@author   Jasper
 *@create   2020/7/7 10:24
 *@describe
 *@update
 */
interface DefaultNavigationCallback : NavigationCallback {
    /**
     * Callback when find the destination.
     *
     * @param postcard meta
     */
    override fun onFound(postcard: Postcard?) {}

    /**
     * Callback after lose your way.
     *
     * @param postcard meta
     */
    override fun onLost(postcard: Postcard?){}

    /**
     * Callback after navigation.
     *
     * @param postcard meta
     */
    override fun onArrival(postcard: Postcard?){}

    /**
     * Callback on interrupt.
     *
     * @param postcard meta
     */
    override fun onInterrupt(postcard: Postcard?){}
}