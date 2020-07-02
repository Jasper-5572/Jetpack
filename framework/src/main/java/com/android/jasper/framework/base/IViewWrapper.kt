package com.android.jasper.framework.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *@author   Jasper
 *@create   2019-11-07 10:39
 *@describe
 *@update
 */
interface IViewWrapper {



    /**
     * 动态添加View
     *
     * @param inflater
     * inflater
     * @param container
     * container
     * @param savedInstanceState
     * savedInstanceState
     *
     * @return view
     */
     fun onCreateWrapperView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?


}