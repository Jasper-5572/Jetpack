package com.android.jasper.framework.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import java.util.ArrayList

/**
 *@author   Jasper
 *@create   2020/6/12 16:33
 *@describe
 *@update
 */
open class JasperFragment : Fragment() {
    private val headerWrapperList by lazy { mutableListOf<IViewWrapper>() }
    private val footWrapperList by lazy { mutableListOf<IViewWrapper>() }

    /**
     * fragment是否执行过[Fragment.onResume]
     */
    private var hasOnResume: Boolean = false

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false

    /**
     * 数据是否加载过了
     */
    var hasLoadData: Boolean = false
        private set

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        lazyLoadDataIfPrepared()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreateRootView(inflater, container, savedInstanceState)?.let { rootView ->
            val result: LinearLayout
            if (rootView is LinearLayout && rootView.orientation == LinearLayout.VERTICAL) {
                result = rootView
                addHeadWrapperView(result, inflater, container, savedInstanceState)
                addFootWrapperView(result, inflater, container, savedInstanceState)
            } else {
                result = LinearLayout(context)
                result.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                result.orientation = LinearLayout.VERTICAL
                addHeadWrapperView(result, inflater, container, savedInstanceState)
                result.addView(rootView, result.childCount - 1)
                addFootWrapperView(result, inflater, container, savedInstanceState)
            }
            return result
        }

        return null

    }

    /**
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @param savedInstanceState Bundle?
     * @return View?
     */
    protected open fun onCreateRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    /**
     *
     * @param result LinearLayout
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @param savedInstanceState Bundle?
     */
    private fun addHeadWrapperView(
        result: LinearLayout,
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        headerWrapperList.forEach { viewWrapper ->
            viewWrapper.onCreateWrapperView(inflater, container, savedInstanceState)?.let {
                require(it.parent == null) { "传入头部装饰者的rootView必须没有父控件！" }
                result.addView(it, 0)
            }
        }
    }

    /**
     *
     * @param result LinearLayout
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @param savedInstanceState Bundle?
     */
    private fun addFootWrapperView(
        result: LinearLayout,
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        footWrapperList.forEach { viewWrapper ->
            viewWrapper.onCreateWrapperView(inflater, container, savedInstanceState)?.let {
                require(it.parent == null) { "传入尾部装饰者的rootView必须没有父控件！" }
                result.addView(it, result.childCount - 1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasOnResume) {
            hasOnResume = true
            lazyLoadDataIfPrepared()
        }
    }

    /**
     * 延迟加载数据
     */
    private fun lazyLoadDataIfPrepared() {
        if (hasOnResume && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    /**
     * 关闭页面
     */
    fun onFinish() {
        (context as? Activity)?.finish()
    }

    /**
     * 关闭页面并通知打开的该页面回调
     * @param intent Intent？ 默认回调空数据给上层页面
     */
    fun onResultOkFinish(intent: Intent? = null) {
        (context as? Activity)?.apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasLoadData = false
    }

    /**
     * 懒加载
     */
    @CallSuper
    open fun lazyLoad() {
    }

}