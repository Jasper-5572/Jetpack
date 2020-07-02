package com.android.jasper.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.jasper.framework.base.IViewWrapper
import com.android.jasper.framework.expansion.setOnClick

/**
 *@author   Mr.Hu(Jc) OrgVTrade
 *@create   2019-11-08 09:38
 *@describe 通用标题Wrapper
 *@update
 */
class TitleViewWrapper constructor(private val initData: TitleViewWrapper.() -> Unit) : IViewWrapper {
    var tvTitle: TextView? = null
        private set
    var ivRightIcon: ImageView? = null
        private set
    var tvRight: TextView? = null
        private set
    var rootView: View? = null
        private set


    @SuppressLint("InflateParams")
    override fun onCreateWrapperView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.base_wrapper_title_layout, null)
        rootView?.apply {
            findViewById<ImageView>(R.id.iv_back)?.setOnClick {
                (inflater.context as? Activity)?.finish()
            }
            tvTitle = findViewById(R.id.tv_wrapper_title_name)
            ivRightIcon = findViewById(R.id.iv_wrapper_right_icon)
            tvRight = findViewById(R.id.tv_wrapper_right_text)
        }
        //初始化控件调用此方法，外部可以在此方法里面拿到对应的控件执行定制化操作
        initData()
        return rootView
    }




}