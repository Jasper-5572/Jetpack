package com.android.jasper.framework.widget

import android.os.CountDownTimer
import android.widget.TextView
import androidx.annotation.CallSuper
import com.android.jasper.framework.expansion.getString
import com.android.jasper.framework.expansion.setString

/**
 *@author   Jasper
 *@create   2020/7/13 15:38
 *@describe
 *@update
 */
open class BaseCountDownTime(
        countDownInterval: Long = 1000L,
        private val textView: TextView?,
        millisInFuture: Long
) : CountDownTimer(millisInFuture, countDownInterval) {
    /**
     * 默认字体文字
     */
    private val defaultText: String = textView?.getString() ?: ""

    /**
     * 默认字体颜色
     */
    private val defaultColor = textView?.textColors

    /**
     * 完成回调方法
     */
    private var onFinishLisener: () -> Unit = {
    }

    private var onTickLisener: (secondUntilFinished: Long) -> Unit = {
    }

    @CallSuper
    fun setOnFinishLisener(onFinishLisener: () -> Unit) {
        this.onFinishLisener = onFinishLisener
    }

    @CallSuper
    fun setOnTickLisener(onTickLisener: (secondUntilFinished: Long) -> Unit) {
        this.onTickLisener = onTickLisener
    }

    @CallSuper
    override fun onFinish() {
        restore()
        this.onFinishLisener()
    }

    @CallSuper
    override fun onTick(millisUntilFinished: Long) {
        onTickLisener(millisUntilFinished / 1000L)
    }


    /**
     * 开始倒计时，设置发送按钮不可点击
     */
    @CallSuper
    fun onStart() {
        textView?.isClickable = false
        start()
    }

    /**
     * 取消定时器，并回到初状态
     */
    @CallSuper
    fun onCancel() {
        cancel()
        restore()
    }

    /**
     * 恢复初始状态
     */
    private fun restore() {
        textView?.isClickable = true
        textView?.setTextColor(defaultColor)
        textView?.setString(defaultText)
    }
}