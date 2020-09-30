package com.android.jasper.framework.live_data

import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 *@author   Jasper
 *@create   2020/8/5 16:17
 *@describe
 *@update
 */
/**
 *
 * @param T
 * @property stickiness Boolean 是否需要粘性
 * @property emptyObserverListener Function0<Unit>
 * @property version Int
 * @constructor
 */
internal class JasperLiveData<T> constructor(
    private val stickiness: Boolean = false,
    private val emptyObserverListener: () -> Unit = {}
) : MutableLiveData<T>() {
    var version = -1
        private set

    override fun setValue(value: T) {
        version++
        super.setValue(value)
    }


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        version = -1
        if (stickiness) {
            super.observe(owner, observer)
        } else {
            super.observe(owner, NotStickinessObserver(observer, this))
        }
    }

    override fun observeForever(observer: Observer<in T>) {
        version = -1
        if (stickiness) {
            super.observeForever(observer)
        } else {
            super.observeForever(NotStickinessObserver(observer, this))
        }
    }

    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
        //如果当前事件没有监听者 则从事件map中移除
        if (!hasObservers()) {
            emptyObserverListener.invoke()
        }
    }

}

private class NotStickinessObserver<T>(
    private val observer: Observer<in T>,
    private val jasperLiveData: JasperLiveData<T>
) : Observer<T> {
    private var version = -1
    override fun onChanged(@Nullable t: T) {
        if (jasperLiveData.version > version) {
            version = jasperLiveData.version
            observer.onChanged(t)
        }

    }


}
