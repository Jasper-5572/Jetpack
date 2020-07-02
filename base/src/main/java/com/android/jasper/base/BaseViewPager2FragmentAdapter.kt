package com.android.jasper.base

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.jasper.framework.util.ResourcesUtils

/**
 *@author   Jasper
 *@create   2020/6/30 16:03
 *@describe
 *@update
 */
open class BaseViewPager2FragmentAdapter<F : Fragment> : FragmentStateAdapter {
    private val fragmentList by lazy { mutableListOf<F>() }

    private val titleList by lazy { mutableListOf<CharSequence>() }

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)
    constructor(fragment: Fragment) : super(fragment)
    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(
        fragmentManager,
        lifecycle
    )


    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    /**
     * 添加Fragment
     * @param fragment F 添加Fragment
     * @param title CharSequence
     * @return KtFragmentAdapter<F>
     */
    fun addFragment(fragment: F, title: CharSequence? = null) {
        title?.let {
            titleList.add(it)
        }
        fragmentList.add(fragment)
    }


    /**
     * 添加Fragment
     * @param fragment F Fragment
     * @param titleRes Int 标题资源id
     * @return FragmentAdapter<F> 当前对象
     */
    open fun addFragment(fragment: F, @StringRes titleRes: Int) {
        titleList.add(ResourcesUtils.getString(titleRes))
        fragmentList.add(fragment)
    }

    open fun getPageTitle(position: Int): CharSequence {
        return if (titleList.isEmpty()) {
            ""
        } else {
            titleList[position]
        }
    }
}