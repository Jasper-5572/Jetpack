package com.android.jasper.base

import androidx.annotation.CallSuper
import androidx.lifecycle.Observer
import com.android.jasper.framework.base.JasperFragment

/**
 *@author   Jasper
 *@create   2020/6/30 16:25
 *@describe
 *@update
 */
open class BaseFragment<VM : BaseViewModel> : JasperFragment() {
    /**
     * 创建ViewModel
     * @return VM?
     */
    protected open fun createViewModel(): VM? = null

    /**
     * viewModel 最好采用lazy{}设置 或者在[JasperFragment.lazyLoad]里面设置
     */
    val viewModel: VM? by lazy {
        createViewModel()?.apply {
            lifecycle.addObserver(this)
            loadingLiveData.observe(this@BaseFragment, Observer { showLoading ->
                if (showLoading) {
                    loadingDialog.onShow(this@BaseFragment)
                } else {
                    loadingDialog.dismiss()
                }
            })
        }
    }

    override fun onResume() {
        if (hasLoadData) {
            onFragmentShow()
        }
        super.onResume()
    }

    /**
     * 加载数据以后 再次显示的时候调用
     */
    @CallSuper
    open fun onFragmentShow() {
    }

    @CallSuper
    open fun onFragmentDismiss() {
    }

    override fun onPause() {
        if (hasLoadData) {
            onFragmentDismiss()
        }
        super.onPause()
    }

    /**
     * 加载对话框
     */
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }


}


