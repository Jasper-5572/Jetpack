package com.android.jasper.jetpack.aidl

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.android.jasper.framework.expansion.safeRun
import com.android.jasper.framework.base.SingletonHolder
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.IBinderProvideAidlInterface

/**
 *@author   Jasper
 *@create   2020/8/7 13:41
 *@describe
 *@update
 */
class MainService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return BinderProvideImpl(this)
    }
}

class MainBinderPool private constructor(private val context: Context) {
    companion object : SingletonHolder<MainBinderPool, Context>(::MainBinderPool)

    /**
     * 主进程调用,添加子进程的binder
     * @param key Int
     * @param binderClass Class<out IBinder>
     */
    fun addBinderClass(key: Int, binderClass: Class<out IBinder>) {
        binderMap[key] = binderClass
    }

    internal val binderMap by lazy { hashMapOf<Int, Class<out IBinder>>() }

    /**
     * 实际是[IBinderProvideAidlInterface]在主进程的代理对象
     */
    private var binderProvider: IBinderProvideAidlInterface? = null

    private val mainServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {}
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                safeRun {

                }
                try {
                    binderProvider = IBinderProvideAidlInterface.Stub.asInterface(service)?.apply {
                        asBinder()?.linkToDeath(binderProviderDeathRecipient, 0)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val binderProviderDeathRecipient by lazy {
        object : IBinder.DeathRecipient {
            override fun binderDied() {
                binderProvider?.asBinder()?.unlinkToDeath(this, 0)
                binderProvider = null
                connectService()
            }

        }
    }


    /**
     * 在需要与主进程通信的进程调用,绑定主进程服务，与主进程通信
     */
    @Synchronized
    fun connectService() {
        val intent = Intent(context, MainService::class.java)
        context.bindService(intent, mainServiceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 一般子进程调用，通过[IBinderProvideAidlInterface]在主进程的代理对象获取对应的Binder
     * @param binderKey Int[addBinderClass]添加的key
     * @return IBinder?
     */
    fun queryBinder(binderKey: Int): IBinder? {
        LogUtils.i("binder:queryBinder()->thread=${Thread.currentThread().name},progress=${AppUtils.getCurrentProcessName()}")
        return binderProvider?.queryBinder(binderKey)
    }
}

/**
 * 子进程binder提供者，由于此Binder与主进程的service绑定 所以运行在主进程
 * @property context Context
 * @constructor
 */
class BinderProvideImpl constructor(private val context: Context) :
    IBinderProvideAidlInterface.Stub() {
    override fun queryBinder(binderKey: Int): IBinder? {
        return MainBinderPool.getInstance(context).binderMap[binderKey]?.newInstance()
    }

}