package com.android.jasper.jetpack.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.R
import com.android.jasper.base.BaseFragment
import com.android.jasper.base.BaseViewModel
import kotlinx.android.synthetic.main.test_fragment_test.*

/**
 *@author   Jasper
 *@create   2020/6/30 16:25
 *@describe
 *@update
 */
class TestFragment : BaseFragment<BaseViewModel>() {

    private lateinit var mTitle: String

    companion object {
        fun newFragment(title: String): TestFragment = TestFragment().apply {
            mTitle = title
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.test_fragment_test, null)
    }

    /**
     * 懒加载（页面显示才调用该方法）
     */
    override fun lazyLoad() {
        super.lazyLoad()
        initData()
        setListener()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        text_view?.setString(mTitle)
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        text_view?.setOnClick {
            it.setString("点击切换$mTitle")
        }
    }
}