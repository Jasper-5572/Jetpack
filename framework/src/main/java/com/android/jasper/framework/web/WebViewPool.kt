package com.android.jasper.framework.web

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import com.android.jasper.framework.JasperFramework
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.NetworkUtils


/**
 *@author   Jasper
 *@create   2020/8/6 17:04
 *@describe
 *@update
 */
class WebViewPool private constructor() {
    private val initWebViewSize = 2
    private val availableWebViewList by lazy { mutableListOf<WebView>() }
    private val usingWebViewList by lazy { mutableListOf<WebView>() }

    fun initWeb() {
        //API28 9.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val currentProcessName = AppUtils.getCurrentProcessName()
            //非主进程要设置Webview数据目录后缀
            if (!AppUtils.checkMainProcess(JasperFramework.INSTANCE.application)) {
                WebView.setDataDirectorySuffix(currentProcessName)
            }
        }
        for (i in 0..initWebViewSize) {
            availableWebViewList.add(createWebView(JasperFramework.INSTANCE.application))
        }
    }

    companion object {
        val INSTANCE by lazy { WebViewPool() }

        @SuppressLint("SetJavaScriptEnabled")
        fun initWebViewSeting(context: Context, webView: WebView) {
            val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webView.layoutParams = params
            val setting = webView.settings
            //设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放。
            setting.setSupportZoom(false)
            //设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
            setting.builtInZoomControls = true
            //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。
            setting.displayZoomControls = false
            //设置在WebView内部是否允许访问文件，默认允许访问true。
            setting.allowFileAccess = true
            setting.allowContentAccess = true
            //设置WebView是否使用预览模式加载界面,默认false
            setting.loadWithOverviewMode = true
            //设置WebView是否使用viewport，当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
            // 当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，
            // 如果页面不包含viewport标签，无法提供一个宽度值，这个时候该方法将被使用。
            setting.useWideViewPort = true
            //设置WebView是否支持多屏窗口，参考WebChromeClient#onCreateWindow，默认false，不支持
            setting.setSupportMultipleWindows(false)
            //设置WebView底层的布局算法，参考WebSettings.LayoutAlgorithm，将会重新生成WebView布局
            setting.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            //设置WebView是否加载图片资源，默认true，自动加载图片
            setting.loadsImagesAutomatically = true
            //设置WebView是否以http、https方式访问从网络加载图片资源，默认false
            setting.blockNetworkImage = false
            //设置WebView是否从网络加载资源，Application需要设置访问网络权限，否则报异常,默认false
            setting.blockNetworkLoads = false
            //设置WebView是否允许执行JavaScript脚本，默认false，不允许
            setting.javaScriptEnabled = true
            //设置WebView运行中的脚本可以是否访问任何原始起点内容，默认true
            setting.allowUniversalAccessFromFileURLs = true
            //设置WebView运行中的一个文件方案被允许访问其他文件方案中的内容，默认值true
            setting.allowFileAccessFromFileURLs = true
            //设置编码格式
            setting.defaultTextEncodingName = "utf-8"
            //设置是否开启数据库存储API权限，默认false，未开启
            setting.databaseEnabled = true
            //设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
            setting.domStorageEnabled = true
            setting.setAppCachePath(webView.context.cacheDir.absolutePath + "/webViewCache")
            setting.setAppCacheEnabled(true)
            //设置是否开启定位功能，默认true，开启定位
            setting.setGeolocationEnabled(true)
            //设置脚本是否允许自动打开弹窗，默认false，不允许
            setting.javaScriptCanOpenWindowsAutomatically = true
            //设置WebView是否需要设置一个节点获取焦点当被回调的时候，默认true
            setting.setNeedInitialFocus(true)
            //重写缓存被使用到的方法，该方法基于Navigation Type，
            // 加载普通的页面，将会检查缓存同时重新验证是否需要加载，
            // 如果不需要重新加载，将直接从缓存读取数据，允许客户端通过指定
            // LOAD_DEFAULT、LOAD_CACHE_ELSE_NETWORK、LOAD_NO_CACHE、LOAD_CACHE_ONLY
            // 其中之一重写该行为方法，默认值LOAD_DEFAULT
            if (NetworkUtils.isConnected(context)) {
                setting.cacheMode = WebSettings.LOAD_DEFAULT
            } else {
                setting.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            }

            //设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为，
            // android.os.Build.VERSION_CODES.KITKAT默认为
            // MIXED_CONTENT_ALWAYS_ALLOW，
            // android.os.Build.VERSION_CODES#LOLLIPOP默认为MIXED_CONTENT_NEVER_ALLOW，
            // 取值其中之一：MIXED_CONTENT_NEVER_ALLOW、MIXED_CONTENT_ALWAYS_ALLOW、MIXED_CONTENT_COMPATIBILITY_MODE.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            //页面白屏问题
//            webView.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
//            webView.setBackgroundResource(R.color.white)
        }
    }


    @Synchronized
    fun getWebView(context: Context): WebView {
        val webView: WebView
        if (availableWebViewList.size > 0) {
            webView = availableWebViewList[0]
            availableWebViewList.removeAt(0)
            (webView.parent as? ViewGroup)?.removeView(webView)
            usingWebViewList.add(webView)
        } else {
            webView = WebView(context)
            initWebViewSeting(context, webView)
            usingWebViewList.add(webView)
        }
        return webView
    }

    fun removeWebView(webView: WebView, viewGroup: ViewGroup? = null) {
        viewGroup?.removeView(webView)
        webView.loadUrl("")
        webView.stopLoading()
        webView.webChromeClient = null
        webView.webViewClient = null
        webView.clearHistory()
        synchronized(this) {
            usingWebViewList.remove(webView)
            if (availableWebViewList.size < initWebViewSize) {
                availableWebViewList.add(webView)
            }
        }
    }

    fun createWebView(context: Context): WebView {
        //Using WebView from more than one process at once with the same data directory is not supported
        return WebView(context).apply {
            initWebViewSeting(context, this)
        }
    }
}