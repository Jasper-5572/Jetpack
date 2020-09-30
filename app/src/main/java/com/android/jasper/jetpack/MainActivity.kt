package com.android.jasper.jetpack

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.base.BaseActivity
import com.android.jasper.framework.live_data.EventMessage
import com.android.jasper.framework.live_data.LiveDataBus
import com.android.jasper.jetpack.page.TestFragment
import com.android.jasper.jetpack.page.home.HomeFragment
import com.android.jasper.jetpack.page.personal.PersonalFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


/**
 *@author   Jasper
 *@create   2020/5/21 16:47
 *@describe
 *@update
 */
class MainActivity : BaseActivity<MainViewModel>() {

    private val tabTitleArray by lazy {
        arrayListOf<String>().apply {
            add("首页")
            add("测试1")
            add("测试2")
            add("测试3")
            add("个人")
        }
    }
    private val fragmentAdapter by lazy {
        object : FragmentStateAdapter(this@MainActivity) {
            override fun getItemCount(): Int = tabTitleArray.size
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> HomeFragment.newFragment()
                    4 -> PersonalFragment.newFragment()
                    else -> TestFragment.newFragment("测试$position")
                }
            }
        }
    }


    override fun createViewModel(): MainViewModel? {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        setListener()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        view_pager2?.apply {
//            (getChildAt(0) as? RecyclerView)?.let {
//                //自定义缓存Item的个数
//                it.setItemViewCacheSize(tabTitleArray.size)
//                //关闭预加载
//                it.layoutManager?.isItemPrefetchEnabled = false
//            }
            isUserInputEnabled = false
            adapter = fragmentAdapter
            tab_layout?.let {
                TabLayoutMediator(it, this, true,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = tabTitleArray[position]
//                        tab.setIcon(R.mipmap.base_ic_logo_icon)
                    }).attach()

            }
        }
        drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        viewModel?.showDrawLayout?.observe(this, Observer {
            if (it) {
                drawer_layout?.openDrawer(GravityCompat.START)
            } else if (drawer_layout?.isOpen == true) {
                drawer_layout?.close()
            }
        })
        viewModel?.showTabLayout?.observe(this, Observer {
            if (it) {
                tab_layout?.visibility = View.VISIBLE
            } else {
                tab_layout?.visibility = View.GONE
            }
        })
        tab_layout?.apply {
            clearOnTabSelectedListeners()
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        view_pager2?.setCurrentItem(it.position, false)
                    }
                }

            })
        }
//        fragmentAdapter.registerFragmentTransactionCallback(object :
//            FragmentStateAdapter.FragmentTransactionCallback() {
//            override fun onFragmentMaxLifecyclePreUpdated(
//                fragment: Fragment,
//                maxLifecycleState: Lifecycle.State
//            ): OnPostEventListener {
//                return OnPostEventListener {
//                    LogUtils.i("fragmentAdapter->onFragmentMaxLifecyclePreUpdated($fragment,$maxLifecycleState)")
//                }
//            }
//        })
        drawer_layout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                viewModel?.showDrawLayout?.value = false
            }
        })
//        LiveDataBus.INSTANCE.sendMessage(EventMessage("drawer_layout",true),true)
        LiveDataBus.INSTANCE.observeMessage<Boolean>(this, "drawer_layout", true) {
            if (it.value) {
                drawer_layout?.openDrawer(GravityCompat.START)
            }
            it.callback.invoke(null)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        LogUtils.i(this.javaClass, "MainActivity:onStart()")
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        LogUtils.i(this.javaClass, "MainActivity:onRestart()")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        LogUtils.i(this.javaClass, "MainActivity:onResume()")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        LogUtils.i(this.javaClass, "MainActivity:onPause()")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        LogUtils.i(this.javaClass, "MainActivity:onStop()")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        LogUtils.i(this.javaClass, "MainActivity:onDestroy()")
//    }


}
