package com.android.jasper.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *@author  Jasper
 *@create   2019-11-16 09:46
 *@describe 加载对话框
 *@update
 */
class LoadingDialog : BaseDialog() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_rv_adapter_type_loading, null)
    }

}