package com.android.jasper.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 *@author   Jasper
 *@create   2019-11-18 14:50
 *@describe 对话框基类
 *@update
 */
/**
 *
 * @property cancelable Boolean
 * @constructor
 */
open class BaseDialog constructor(private val cancelable: Boolean = false,
                                  @StyleRes private val dialogThemeResId: Int = R.style.DialogDefaultStyle) : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        super.onActivityCreated(savedInstanceState)
    }


    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.let { window ->
                context?.let { thisContext ->
                    window.setBackgroundDrawable(ContextCompat.getDrawable(thisContext, android.R.color.transparent))
                    (thisContext.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.let { windowManager ->
                        val dm = DisplayMetrics()
                        windowManager.defaultDisplay.getMetrics(dm)
                        window.setLayout(windowWidth(dm.widthPixels), windowHeight(dm.heightPixels))
                    }
                }
                initDialogStyle(it,window)
            }

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val nonNullContext = requireContext()
        return Dialog(nonNullContext, dialogThemeResId).apply {
            setCancelable(cancelable)
            setCanceledOnTouchOutside(cancelable)
        }
    }

    /**
     * 设置对话框的风格 如设置显示位置
     * @param dialog Dialog
     * @param dialogWindow Window
     */
    open fun initDialogStyle(dialog: Dialog, dialogWindow: Window) {

    }

    open fun windowWidth(widthPixels: Int): Int {
        return (widthPixels * 0.8).toInt()
    }

    open fun windowHeight(heightPixels: Int): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun onShow(fragment: Fragment) {
        if (dialog?.isShowing == true) {
            return
        }
        show(fragment.parentFragmentManager, this.javaClass.name)
    }
    open fun onShow(fragmentActivity: FragmentActivity) {
        if (dialog?.isShowing == true) {
            return
        }
        show(fragmentActivity.supportFragmentManager, this.javaClass.name)
    }
    override fun dismiss() {
        if ((dialog?.isShowing == true).not()) {
            return
        }
        super.dismiss()

    }
}