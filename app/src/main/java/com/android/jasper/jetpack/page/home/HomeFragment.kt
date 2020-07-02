package com.android.jasper.jetpack.page.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.android.jasper.base.BaseFragment
import com.android.jasper.base.BaseViewModel
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.MainViewModel
import com.android.jasper.jetpack.R
import kotlinx.android.synthetic.main.home_fragment_home.*

/**
 *@author   Jasper
 *@create   2020/7/1 17:15
 *@describe
 *@update
 */
class HomeFragment : BaseFragment<BaseViewModel>() {
    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }

    companion object {
        fun newFragment(): HomeFragment = HomeFragment().apply {

        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtils.i("viewPager->PersonalFragment.onCreateView()")
        return inflater.inflate(R.layout.home_fragment_home, null)
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


    }

    /**
     * 设置监听
     */
    private fun setListener() {
        iv_menu?.setOnClick {
            mainViewModel.showDrawLayout.value = true
        }
//        parentFragmentManager.
//        iv_menu?.setOnClick {
//            fragmentManager?.addOnBackStackChangedListener(){
//
//            }
//            drawer_layout?.openDrawer(GravityCompat.START)
//        }

    }
}