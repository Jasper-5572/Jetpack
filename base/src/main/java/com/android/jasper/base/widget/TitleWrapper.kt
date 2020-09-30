package com.android.jasper.base.widget

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.jasper.base.R

/**
 *@author   Jasper
 *@create   2020/7/9 11:35
 *@describe
 *@update
 */
class TitleWrapper constructor(private val initView: TitleWrapper.() -> Unit) :
    ActivityViewWrapper {
    var tvTitle: TextView? = null
        private set

    var toolTar: Toolbar? = null
        private set

    override fun getLayoutResource(): Int {
        return R.layout.base_activity_title_wrapper_layout
    }

    override fun initView(activity: AppCompatActivity) {
        activity.findViewById<Toolbar>(R.id.tool_bar)?.also {
            activity.setSupportActionBar(it)
            toolTar = it
        }
        activity.supportActionBar?.apply {
            //左侧按钮：可见+可用+更换图标
            setHomeAsUpIndicator(R.drawable.base_vector_white_back)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = ""
        }
        tvTitle = activity.findViewById(R.id.toolbar_title)
        initView()
    }

}