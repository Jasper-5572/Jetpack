package com.android.jasper.framework.util

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *@author   Jasper
 *@create   2020/5/21 16:56
 *@describe
 *@update
 */
object DispatchersUtils {

    fun runOnUiThread(){
        GlobalScope.launch() {  }
    }



    fun run(context: CoroutineContext = EmptyCoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit){
        GlobalScope.launch() {  }
    }
}