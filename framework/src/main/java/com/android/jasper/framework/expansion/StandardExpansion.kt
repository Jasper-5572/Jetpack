package com.android.jasper.framework.expansion

import com.android.jasper.framework.JasperFramework
import java.lang.Exception

/**
 *@author   Jasper
 *@create   2020/7/14 11:33
 *@describe
 *@update
 */
inline fun <R> safeRun(block: () -> R){
    try {
        block()
    } catch (e: Exception) {
        if (JasperFramework.appDebug) {
            e.printStackTrace()
        }
    }

}