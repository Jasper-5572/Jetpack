@file:Suppress("UNCHECKED_CAST")

package com.android.jasper.framework.expansion

import android.view.View

/**
 *@author   Mr.Hu(Jc) OrgVTrade
 *@create   2019-11-07 18:54
 *@describe
 *@update
 */
/**
 * 带延迟过滤的点击事件View扩展
 * @receiver T
 * @param time Long 延迟时间，默认600毫秒
 * @param block Function1<T, Unit> 函数
 */
fun <T : View> T.setOnClick(time: Long = 600, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else -601
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}





