package com.android.jasper.framework.adapter.fragment

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.android.jasper.framework.util.ResourcesUtils

/**
 *@author   Jasper
 *@create   2020/6/12 16:17
 *@describe FragmentAdapter
 *@update
 */
open class FragmentAdapter<F : Fragment>(
    fragmentManager: FragmentManager,
    behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) : FragmentPagerAdapter(fragmentManager, behavior) {
    /**
     * 标题列表
     */
    val titleList by lazy { mutableListOf<CharSequence>() }

    /**
     * fragment列表
     */
    val fragmentList by lazy { mutableListOf<F>() }
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (titleList.isEmpty()) {
            super.getPageTitle(position)
        } else {
            titleList[position]
        }
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

    /**
     * 根据position获取对应的Fragment
     * @param position Int position
     * @return F?  Fragment
     */
    fun getFragment(position: Int): F? {
        return if (fragmentList.size > position) fragmentList[position] else null
    }

    /**
     * 清除数据
     */
    fun clear() {
        titleList.clear()
        fragmentList.clear()
    }
}