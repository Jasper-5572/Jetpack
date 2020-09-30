package com.android.jasper.framework.base

import com.android.jasper.framework.JasperFramework
import kotlinx.coroutines.*

/**
 *@author   Jasper
 *@create   2020/7/13 14:31
 *@describe
 *@update
 */
/**
 *
 * @receiver CoroutineScope
 */
fun CoroutineScope.launcher(block: suspend CoroutineScope.() -> Unit): Launcher {
    return Launcher(this, block)
}

//private fun CoroutineScope.launcherInterval(end: Long, /* -1 表示永远不结束, 可以修改*/
//                                    period: Long,
//                                    unit: TimeUnit,
//                                    start: Long = 0,
//                                    initialDelay: Long = period,
//                                    block: suspend CoroutineScope.() -> Unit): Launcher {
//
//    return IntervalLauncher(this, block)
//}

/**
 * 安全处理
 * @receiver CoroutineScope
 */
fun CoroutineScope.safeLaunch(onException: (e: Throwable) -> Unit = {}, onComplete: () -> Unit = {}, block: suspend CoroutineScope.() -> Unit): Job {
    return launch(CoroutineExceptionHandler { _, throwable ->
        run {
            if (JasperFramework.appDebug){
                throwable.printStackTrace()
            }
            //处理异常
            onException(throwable)
        }
    }) {
        try {
            block.invoke(this)
        } finally {
            onComplete()
        }
    }
}

/**
 * 发射器
 * @property coroutineScope CoroutineScope
 * @property block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
 * @constructor
 */
open class Launcher(private val coroutineScope: CoroutineScope, private val block: suspend CoroutineScope.() -> Unit) {
    protected var onException: (e: Throwable) -> Unit = {}
    protected var onComplete: () -> Unit = {}
    open fun start(): Job {
        return coroutineScope.safeLaunch(onException, onComplete, block)
    }

    /**
     * 异常处理
     * @param onFunction Function1<[@kotlin.ParameterName] Throwable, Unit>
     * @return Launcher
     */
    fun onException(onFunction: (e: Throwable) -> Unit = {}): Launcher {
        onException = onFunction
        return this
    }

    fun onComplete(onFunction: () -> Unit = {}): Launcher {
        onComplete = onFunction
        return this
    }


}


private class IntervalLauncher(private val coroutineScope: CoroutineScope, private val block: suspend CoroutineScope.() -> Unit) : Launcher(coroutineScope, block) {
    internal var end: Long = -1
    internal var period: Long = 0
    internal var start: Long = -1
    internal var initialDelay: Long = 0

    override fun start(): Job {
        return coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
            run {
                //处理异常
                onException(throwable)
            }
        }) {
            try {
                block.invoke(this)
            } finally {
                onComplete()
            }

        }
    }

}

