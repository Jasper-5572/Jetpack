package com.android.jasper.jetpack.page.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.jasper.base.BaseFragment
import com.android.jasper.base.BaseViewModel
import com.android.jasper.framework.PermissionResult
import com.android.jasper.framework.ProxyFragmentManager
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.live_data.EventMessage
import com.android.jasper.framework.live_data.LiveDataBus
import com.android.jasper.framework.network.JasperDownload
import com.android.jasper.framework.util.AppUtils
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.framework.util.ScreenUtils
import com.android.jasper.jetpack.MainViewModel
import com.android.jasper.jetpack.R
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.home_fragment_home.*
import kotlinx.android.synthetic.main.home_layout_left_drawer_layout.*

/**
 *@author   Jasper
 *@create   2020/7/1 17:15
 *@describe
 *@update
 */
class HomeFragment : BaseFragment<BaseViewModel>() {
    private val mainViewModel by lazy { ViewModelProvider(requireActivity()).get(MainViewModel::class.java) }
    private val drawerListener by lazy { DrawerListener(mainViewModel, drawer_layout) }
    private val fragmentAdapter by lazy {
        object : FragmentStateAdapter(this@HomeFragment) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment {
                return HomeTestFragment.newFragment("测试$position")
//                return when (position) {
//                    0 -> HomeFragment.newFragment()
//                    4 -> PersonalFragment.newFragment()
//                    else -> HomeTestFragment.newFragment("测试$position")
//                }
            }
        }
    }

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
//        drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //设置左侧抽屉宽度
//        layout_left_drawer?.layoutParams?.width = (ScreenUtils.getScreenWidth(context) * 0.8).toInt()
        view_pager2?.apply {
            (getChildAt(0) as? RecyclerView)?.let {
                //自定义缓存Item的个数
                it.setItemViewCacheSize(3)
                //关闭预加载
                it.layoutManager?.isItemPrefetchEnabled = false
            }
//            isUserInputEnabled = false
            adapter = fragmentAdapter
            tab_layout?.let {
                TabLayoutMediator(it, this, true,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = "Home[$position]"
//                        tab.setIcon(R.mipmap.base_ic_logo_icon)
                    }).attach()

            }
        }

    }

    override fun onResume() {
        super.onResume()
        layout_left_drawer?.layoutParams?.width =
            (ScreenUtils.getScreenWidth(context) * 0.8).toInt()
        drawer_layout?.removeDrawerListener(drawerListener)
        drawer_layout?.addDrawerListener(drawerListener)
    }

    override fun onPause() {
        super.onPause()
        drawer_layout?.closeDrawer(GravityCompat.START)
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        iv_menu?.setOnClick {
//            mainViewModel.showTabLayout.value = false
//            drawer_layout?.openDrawer(GravityCompat.START,true)
            LiveDataBus.INSTANCE.sendMessage(EventMessage("drawer_layout", true))
        }

val downloadUrl="https://iex-prod.oss-accelerate.aliyuncs.com/adminserver/fbad2b8596464a4586306640edca456f.apk"
        tv_download?.setOnClick {
            ProxyFragmentManager.of(this).requestPermissionsResult(permissionSet = HashSet<String>().apply {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }, permissionResult = object : PermissionResult {
                override fun onGrant() {
                    JasperDownload. downloadForever(downloadUrl,onProgress = {progress: Double, totalReadBytes: Long, contentLength: Long->
                        LogUtils.i("JasperDownload->onProgress:('progress=$progress','totalReadBytes=$totalReadBytes','contentLength=$contentLength')")
                    },onSuccess = {
                        AppUtils.installApk(requireContext(),it.path?:"")
                    })
                }

            })


//            JasperDownload.INSTANCE.download("https://dl.google.com/dl/android/studio/install/3.5.2.0/android-studio-ide-191.5977832-windows.exe") { progress: Double, totalReadBytes: Long, contentLength: Long ->
//                LogUtils.i("JasperDownload->onProgress:('progress=$progress','totalReadBytes=$totalReadBytes','contentLength=$contentLength')")
//            }
        }
    }

    private class DrawerListener constructor(
        private val viewModel: MainViewModel,
        private val drawerLayout: DrawerLayout
    ) : DrawerLayout.SimpleDrawerListener() {
        override fun onDrawerStateChanged(newState: Int) {
            super.onDrawerStateChanged(newState)
            if (newState == ViewDragHelper.STATE_SETTLING) {
                LogUtils.i("drawer_layout onDrawerStateChanged(isShown)${drawerLayout.isOpen}")
                viewModel.showTabLayout.value = drawerLayout.isOpen
            }
            LogUtils.i("drawer_layout onDrawerStateChanged()$newState")
        }

        private fun animZoom(target: View, slideOffset: Float) {
            target.pivotX = 0f
            target.pivotY = target.paddingBottom.toFloat()
            target.scaleX = slideOffset
            target.scaleY = slideOffset
            target.alpha = slideOffset * 255
            target.translationY = slideOffset
        }


//            override fun onDrawerOpened(drawerView: View) {
//                super.onDrawerOpened(drawerView)
//                mainViewModel.showTabLayout.value = false
//            }
    }
}