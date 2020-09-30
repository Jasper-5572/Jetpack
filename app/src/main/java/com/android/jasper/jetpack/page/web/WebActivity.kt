package com.android.jasper.jetpack.page.web

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.jasper.framework.util.NetworkUtils
import com.android.jasper.base.BaseActivity
import com.android.jasper.base.widget.ActivityViewWrapper
import com.android.jasper.base.widget.TitleWrapper
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.live_data.EventMessage
import com.android.jasper.framework.live_data.LiveDataBus
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.framework.web.WebViewPool
import com.android.jasper.jetpack.IWebAidlInterface
import com.android.jasper.jetpack.MainViewModel
import com.android.jasper.jetpack.R
import com.android.jasper.jetpack.aidl.MainBinderPool
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 *@author   Jasper
 *@create   2020/8/6 11:46
 *@describe
 *@update
 */
@Route(path = "/app/webView")
class WebActivity : BaseActivity<MainViewModel>() {
    private val webView by lazy {
        WebViewPool.INSTANCE.getWebView(this).apply {
            fragment_layout?.addView(this)
        }
    }
    private var tvTitle: TextView? = null
    override fun createTitleWrapper(): ActivityViewWrapper? =
        TitleWrapper { this@WebActivity.tvTitle = tvTitle }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initData()
        setListener()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        webView.loadUrl("https://www.baidu.com")
        LogUtils.i("binder:initData()->thread=${Thread.currentThread().name},progress=${AppUtils.getCurrentProcessName()}")
        WebBinder.get(this)?.gotoLogin()
    }


    override fun onDestroy() {
        super.onDestroy()
        WebViewPool.INSTANCE.removeWebView(webView)
    }

    /**
     * 设置监听
     */
    private fun setListener() {

    }

}