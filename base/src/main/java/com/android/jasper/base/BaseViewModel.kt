package com.android.jasper.base

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.android.jasper.framework.util.LogUtils

/**
 *@author   Jasper
 *@create   2020/7/1 13:46
 *@describe
 *@update
 */
open class BaseViewModel : ViewModel(), DefaultLifecycleObserver {
    val dataLoading = MutableLiveData<Boolean>()
    /**
     * fragment是否执行过[Fragment.onResume]
     */
    private var hasOnResume: Boolean = false

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false

    /**
     * 数据是否加载过了
     */
    var hasLoadData: Boolean = false
        private set

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        isViewPrepare = true
        lazyLoadDataIfPrepared()

    }

    /**
     * 延迟加载数据
     */
    private fun lazyLoadDataIfPrepared() {
        if (hasOnResume && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (!hasOnResume) {
            hasOnResume = true
            lazyLoadDataIfPrepared()
        }
    }


    open fun lazyLoad() {
    }

}