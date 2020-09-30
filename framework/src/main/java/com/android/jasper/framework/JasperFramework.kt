package com.android.jasper.framework

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Process
import com.android.jasper.framework.base.Launcher
import com.android.jasper.framework.base.launcher
import com.android.jasper.framework.base.safeLaunch
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 *@author   Jasper
 *@create   2020/5/18 15:08
 *@describe JasperFramework框架入口
 *@update
 */
class JasperFramework private constructor() : CoroutineScope {
    private val job: Job by lazy { Job() }
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job
    private val appDebug by lazy {
        application.applicationContext?.applicationInfo?.let {
            return@let it.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } ?: false
    }

    /**
     * activity集合
     */
    private val activities by lazy {
        mutableListOf<Activity>()
    }

    /**
     * application
     */
    var application: Application by NotNullSingleValueVar()
        private set

    /**
     * 当前显示的activity
     */
    var showActivity: Activity? = null
        private set

    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JasperFramework() }

        /**
         * app是否是debug
         */
        val appDebug by lazy { INSTANCE.appDebug }

        /**
         *
         * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
         * @return Launcher
         */
        fun launcher(block: suspend CoroutineScope.() -> Unit): Launcher =
            INSTANCE.launcher { block.invoke(this) }

        /**
         *
         * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
         * @return Job
         */
        fun safeLaunch(block: suspend CoroutineScope.() -> Unit): Job =
            INSTANCE.safeLaunch { block.invoke(this) }
    }

    /**
     * 初始化
     * @param application Application
     * @param jasperConfiguration JasperConfiguration?
     */
    fun initialize(application: Application, jasperConfiguration: JasperConfiguration? = null) {
        this.application = application
        //解析xml配置并更新动态配置
        JasperConfigurationManager.INSTANCE.initialize(application, jasperConfiguration)
        //注册Activity生命周期的监听
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityDestroyed(activity: Activity) {
                //从列表移除
                activities.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                //添加到列表
                activities.add(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                //当前显示的activity
                showActivity = activity
            }

        })

    }

    /**
     * 退出APP
     * @param killProcess Boolean 是否杀掉其它进程(推送==)
     */
    fun exitApp(killProcess: Boolean = true) {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        if (killProcess) {
            (application.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let {
                it.runningAppProcesses?.forEach { runAppProcessInfo ->
                    if (runAppProcessInfo.pid != Process.myPid()) {
                        Process.killProcess(runAppProcessInfo.pid)
                    }
                }
            }
        }
        job.cancel()
        showActivity = null
        activities.filterNot { it.isFinishing }.forEach { it.finish() }
        activities.clear()
        application.onTerminate()
        Process.killProcess(Process.myPid())
    }
}

//定义一个属性管理类，进行非空和重复赋值的判断
private class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
            ?: throw IllegalStateException("${JasperFramework::class.java.simpleName}not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value
        else throw IllegalStateException("${JasperFramework::class.java.simpleName}has initialize")
    }
}



