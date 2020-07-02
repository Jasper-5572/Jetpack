package com.android.jasper.jetpack

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.base.BaseActivity
import com.android.jasper.base.BaseViewPager2FragmentAdapter
import com.android.jasper.jetpack.lifecycle.TestLifecycle
import com.android.jasper.jetpack.page.TestFragment
import com.android.jasper.jetpack.page.home.HomeFragment
import com.android.jasper.jetpack.page.personal.PersonalFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

/**
 *@author   Jasper
 *@create   2020/5/21 16:47
 *@describe
 *@update
 */
class MainActivity : BaseActivity() {

    private val fragmentAdapter by lazy {
        BaseViewPager2FragmentAdapter<Fragment>(
            this@MainActivity
        )
    }

    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
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
        fragmentAdapter.apply {
            addFragment(HomeFragment.newFragment(), "首页")
            addFragment(TestFragment.newFragment("测试"), "测试")
            addFragment(TestFragment.newFragment("测试2"), "测试2")
            addFragment(TestFragment.newFragment("测试3"), "测试3")
            addFragment(PersonalFragment.newFragment(), "个人")
        }
        view_pager2?.apply {
            adapter = fragmentAdapter
            tab_layout?.let {
                TabLayoutMediator(it, this, true,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = fragmentAdapter.getPageTitle(position)
                    }).attach()
            }
            this.offscreenPageLimit=fragmentAdapter.itemCount-1
        }
        drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        viewModel.showDrawLayout.observe(this, Observer {
            if (it) {
                drawer_layout?.openDrawer(GravityCompat.START)
            } else if (drawer_layout?.isOpen == true) {
                drawer_layout?.close()
            }

        })
        drawer_layout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                viewModel.showDrawLayout.value = false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        LogUtils.i(this.javaClass, "MainActivity:onStart()")
    }

    override fun onRestart() {
        super.onRestart()
        LogUtils.i(this.javaClass, "MainActivity:onRestart()")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i(this.javaClass, "MainActivity:onResume()")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.i(this.javaClass, "MainActivity:onPause()")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.i(this.javaClass, "MainActivity:onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i(this.javaClass, "MainActivity:onDestroy()")
    }


    private suspend fun getToken(initToken: Int): Int {
        delay(500L)
        return initToken + 3
    }

    private fun testCoroutines() {

        Thread {
            LogUtils.i(
                this@MainActivity.javaClass,
                "testCoroutines ，当前线程1:${Thread.currentThread().name}"
            )
            MainScope().launch {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程2MainScope:${Thread.currentThread().name}"
                )
            }

            GlobalScope.launch {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程3:${Thread.currentThread().name}"
                )
            }

            GlobalScope.launch(context = Dispatchers.IO) {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程 Dispatchers.IO:${Thread.currentThread().name}"
                )
            }
            GlobalScope.launch(context = Dispatchers.Main) {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程Dispatchers.Main:${Thread.currentThread().name}"
                )
            }
            GlobalScope.launch(context = Dispatchers.Default) {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程 Dispatchers.Default:${Thread.currentThread().name}"
                )
            }
            GlobalScope.launch(context = Dispatchers.Unconfined) {
                LogUtils.i(
                    this@MainActivity.javaClass,
                    "testCoroutines ，当前线程Dispatchers.Unconfined:${Thread.currentThread().name}"
                )
            }
        }.start()

//
//        var token = 1
//        LogUtils.i(
//            this@MainActivity.javaClass,
//            "testCoroutines ，token:$token ,时间:${System.currentTimeMillis()}"
//        )
//        GlobalScope.launch {
//            token = getToken(token)
//            LogUtils.i(
//                this@MainActivity.javaClass,
//                "testCoroutines ，token1:$token ,时间:${System.currentTimeMillis()}"
//            )
//            delay(1000)
//            token = getToken(token)
//            LogUtils.i(
//                this@MainActivity.javaClass,
//                "testCoroutines ，token1:$token ,时间:${System.currentTimeMillis()}"
//            )
//        }
//        LogUtils.i(
//            this@MainActivity.javaClass,
//            "testCoroutines ，token:$token ,时间:${System.currentTimeMillis()}"
//        )
//        GlobalScope.launch {
//            token = getToken(token)
//            LogUtils.i(
//                this@MainActivity.javaClass,
//                "testCoroutines ，token2:$token ,时间:${System.currentTimeMillis()}"
//            )
//            delay(1000)
//            token = getToken(token)
//            LogUtils.i(
//                this@MainActivity.javaClass,
//                "testCoroutines ，token2:$token ,时间:${System.currentTimeMillis()}"
//            )
//        }
    }
}
