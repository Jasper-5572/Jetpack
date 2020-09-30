package com.android.jasper.framework.live_data

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 *@author   Jasper
 *@create   2020/7/13 11:40
 *@describe
 *@update
 */
class LiveDataBus private constructor() {

    private val busMap by lazy { hashMapOf<String, EventLiveData<EventMessage<*>>?>() }

    companion object {
        val INSTANCE by lazy { LiveDataBus() }
    }

    /**
     * 发送消息
     * @param message EventMessage<D>
     * @param stickiness Boolean 是否是粘性消息 如果[message]key对应的消息可能为粘性 此处一定要设置为true，
     * 其实这里可以默认为true,在[observeMessage]和[observeMessageForever]订阅的时候区分是否接收粘性消息，
     * 此处之所以设置默认为false主要是为了防止当[EventLiveData]没有订阅者的时候[busMap]内存占用太大
     */
    fun <D> sendMessage(message: EventMessage<D>, stickiness: Boolean = false) {
        getLiveData<D>(message.key, stickiness, false)?.value = message
    }


    /**
     * 异步发送消息 此消息无法控制先后顺序
     * @param message EventMessage<D>
     * @param stickiness Boolean 是否是粘性消息  @see[sendMessage]
     */
    fun <D> postMessage(message: EventMessage<D>, stickiness: Boolean = false) {
        getLiveData<D>(message.key, stickiness, false)?.postValue(message)
    }

    /**
     * 接收消息
     * @param owner LifecycleOwner
     * @param key String
     * @param stickiness Boolean 是否接收粘性消息 @see[sendMessage]
     * @param observer Function1<[@kotlin.ParameterName] EventMessage<D>, Unit>
     */
    fun <D> observeMessage(
        owner: LifecycleOwner,
        key: String,
        stickiness: Boolean = false,
        observer: (message: EventMessage<D>) -> Unit = {}
    ) {
        getLiveData<D>(key, stickiness, true)?.observe(owner, Observer {
            it?.let {
                //如果订阅粘性消息 则所有消息都返回
                if (stickiness) {
                    observer.invoke(it)
                }
                //否则只返回非粘性消息
                else if (!it.stickiness) {
                    observer.invoke(it)
                }
            }
        })
    }

    /**
     *
     * @param key String
     * @param stickiness Boolean  是否接收粘性消息 @see[sendMessage]
     * @param observer Function1<[@kotlin.ParameterName] EventMessage<D>, Unit>
     */
    fun <D> observeMessageForever(
        key: String,
        stickiness: Boolean = false,
        observer: (message: EventMessage<D>) -> Unit = {}
    ) {
        getLiveData<D>(key, stickiness, true)?.observeForever {
            it?.let {
                //如果订阅粘性消息 则所有消息都返回
                if (stickiness) {
                    observer.invoke(it)
                }
                //否则只返回非粘性消息
                else if (!it.stickiness) {
                    observer.invoke(it)
                }

            }
        }
    }

    /**
     *
     * @param key String
     * @param stickiness Boolean
     * @param needCreate Boolean
     * @return MutableLiveData<EventMessage<D>>?
     */
    private fun <D> getLiveData(
        key: String,
        stickiness: Boolean = false,
        needCreate: Boolean
    ): MutableLiveData<EventMessage<D>>? {
        //如果是粘性消息或者订阅消息 都要创建LiveData
        if (stickiness || needCreate) {
            if (!busMap.containsKey(key)) {
                busMap[key] = EventLiveData {
                    //非粘性消息，如果没有订阅者则从map中移除LiveData
                    if (!stickiness) {
                        busMap.remove(key)
                    }
                }
            }
        }
        @Suppress("UNCHECKED_CAST")
        return (busMap[key] as? MutableLiveData<EventMessage<D>>)
    }

}

/**
 * 消息体
 * @param D
 * @property key String 消息key
 * @property value D 消息内容
 * @property stickiness Boolean 是否是粘性消息
 * @property callback Function1<[@kotlin.ParameterName] Bundle, Unit>
 * @constructor
 */
data class EventMessage<D> @JvmOverloads constructor(
    /**
     * 消息key
     */
    val key: String,
    val value: D,
    internal var stickiness: Boolean = false,
    val callback: (bundle: Bundle?) -> Unit = {}
)

class EventLiveData<T : EventMessage<*>> constructor(private val emptyObserverListener: () -> Unit = {}) :
    MutableLiveData<T>() {
    var version = -1
        private set

    override fun setValue(value: T) {
        version++
        super.setValue(value)
    }


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, EventObserver(version, observer, this))
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(EventObserver(version, observer, this))
    }

    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
        //如果当前事件没有监听者 则从事件map中移除
        if (!hasObservers()) {
            emptyObserverListener.invoke()
        }
    }
}

private class EventObserver<T : EventMessage<*>>(
    observeLiveDataVersion: Int,
    private val observer: Observer<in T>,
    private val eventLiveData: EventLiveData<T>
) : Observer<T?> {
    /**
     * 记录
     */
    private var version = observeLiveDataVersion

    override fun onChanged(t: T?) {
        //如果当前观察者的版本号大于等于LiveData的版本号表示是历史消息（实际不可能大于只会出现等于） 即粘性消息
        t?.stickiness = version >= eventLiveData.version
        version = eventLiveData.version
        observer.onChanged(t)
    }


}

