package com.android.jasper.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.jasper.framework.base.JasperFragment
import kotlin.reflect.KProperty

/**
 *@author   Jasper
 *@create   2020/6/30 16:25
 *@describe
 *@update
 */
open class BaseFragment<VM : BaseViewModel> : JasperFragment() {
    /**
     * viewModel 最好采用lazy{}设置 或者在[JasperFragment.lazyLoad]里面设置
     */
    var viewModel: VM? = null
        protected set(value) {
            value?.let {
                lifecycle.addObserver(it)
                it.dataLoading.observe(this@BaseFragment, Observer { showLoading ->
                    if (showLoading) {
                        loadingDialog.onShow(this@BaseFragment)
                    } else {
                        loadingDialog.dismiss()
                    }
                })
            }
            field = value
        }

    /**
     * 加载对话框
     */
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }


}


