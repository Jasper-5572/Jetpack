package com.android.jasper.base

import android.os.Handler
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *@author   Jasper
 *@create   2020/7/1 13:46
 *@describe
 *@update
 */
open class BaseViewModel : ViewModel(), DefaultLifecycleObserver {

    val loadingLiveData by lazy { MutableLiveData<Boolean>() }

    val toastStringLiveData by lazy { MutableLiveData<String>() }

    val toastStringResLiveData by lazy { MutableLiveData<Int>() }

    val toastThrowableLiveData by lazy { MutableLiveData<Throwable>() }

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

fun ViewModel.launch(
    onError: (e: Throwable) -> Unit = {},
    onFinally: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            run {
                onError(throwable)
            }
        }) {
        try {
            block.invoke(this)
        } finally {
            onFinally()
        }
    }
}

inline fun <reified Rep,reified V:ViewModel> ViewModelStoreOwner.getViewModel(repository: Rep): V {
    return ViewModelProvider(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Rep::class.java).newInstance(repository)
        }
    }).get(V::class.java)
}
inline fun <reified V:ViewModel> ViewModelStoreOwner.getViewModel(): V {
    return ViewModelProvider(this).get(V::class.java)
}
