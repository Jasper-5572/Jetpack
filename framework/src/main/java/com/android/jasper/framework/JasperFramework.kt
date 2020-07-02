package com.android.jasper.framework

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Process
import com.android.jasper.framework.util.AppUtils

/**
 *@author   Jasper
 *@create   2020/5/18 15:08
 *@describe JasperFramework框架入口
 *@update
 */
class JasperFramework private constructor() {
    /**
     * activity集合
     */
    private val activities by lazy {
        mutableListOf<Activity>()
    }

    /**
     * app状态监听
     */
    private val appStateListenerList by lazy {
        mutableListOf<AppStateListener>()
    }

    /**
     * 正在start状态的activity的数量，用于判断前后台切换的状态
     */
    private var activityStartCount: Int = 0

    /**
     * application
     */
    var application: Application? = null
        private set

    /**
     * 当前显示的activity
     */
    var showActivity: Activity? = null
        private set

    companion object {
        val INSTANCE by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JasperFramework() }
    }

    fun initialize(application: Application, jasperConfiguration: JasperConfiguration? = null) {
        //检测是否是主进程
        if (!AppUtils.checkMainProcess(application)) {
            return
        }
        //检测是否已经初始化过
        require(this.application == null) {
            "${JasperFramework::class.java.simpleName}has initialize"
        }


        this.application = application
        //解析xml配置并更新动态配置
        JasperConfigurationManager.INSTANCE.initialize(application, jasperConfiguration)
        //注册Activity生命周期的监听
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {
                activityStartCount++
                //数值从0变到1说明是从后台切到前台
                if (activityStartCount == 1) {
                    //从后台切到前台
                    appStateListenerList.forEach {
                        it.onFront(activity)
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                //从列表移除
                activities.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityStopped(activity: Activity) {
                activityStartCount--
                //数值从1到0说明是从前台切到后台
                if (activityStartCount == 0) {
                    //从前台切到后台
                    appStateListenerList.forEach {
                        it.onBack(activity)
                    }

                }
            }

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
            (application?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let {
                it.runningAppProcesses?.forEach { runAppProcessInfo ->
                    if (runAppProcessInfo.pid != Process.myPid()) {
                        Process.killProcess(runAppProcessInfo.pid)
                    }
                }
            }
        }
        showActivity = null
        activities.filterNot {
            it.isFinishing
        }.forEach { it.finish() }
        activities.clear()


        application?.onTerminate()
        Process.killProcess(Process.myPid())


    }


    /**
     * 添加app状态监听（前台 后台）
     * @param appStateListener AppStateListener
     */
    fun registerAppStateListener(appStateListener: AppStateListener) {
        appStateListenerList.add(appStateListener)
    }

    /**
     * 添加app状态监听（前台 后台）
     * @param appStateListener AppStateListener
     */
    fun unRegisterAppStateListener(appStateListener: AppStateListener) {
        appStateListenerList.remove(appStateListener)
    }
}


interface AppStateListener {
    /**
     * 从后台切换到前台
     * activity 当前显示的activity
     */
    fun onFront(activity: Activity)

    /**
     * 从前台切换到后台
     * activity 最后一个显示的activity
     */
    fun onBack(activity: Activity)
}


