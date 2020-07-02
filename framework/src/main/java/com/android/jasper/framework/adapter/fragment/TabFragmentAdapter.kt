package com.android.jasper.framework.adapter.fragment

import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.android.jasper.framework.base.JasperFragment
import com.google.android.material.tabs.TabLayout

/**
 *@author   Jasper
 *@create   2020/6/12 16:56
 *@describe
 *@update
 */
class TabFragmentAdapter<F>(
    fragmentManager: FragmentManager,
    behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) : FragmentAdapter<F>(fragmentManager, behavior) where F : JasperFragment, F : TabFragmentListener {

    /**
     * 关联tabLayout和viewPager
     * @param viewPager ViewPager
     * @param tabLayout TabLayout
     * @param tabViewAgent TabViewAgent?
     */
    fun prepare(viewPager: ViewPager, tabLayout: TabLayout, tabViewAgent: TabViewAgent? = null) {
        viewPager.adapter = this
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    fragmentUnselected(it.position)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    fragmentSelected(it.position)
                }
            }

        })
        tabViewAgent?.prepare(this)
    }

    /**
     *
     * @param position Int
     */
    private fun fragmentSelected(position: Int) {
        getFragment(position)?.apply {
            if (hasLoadData) {
                onFragmentSelected()
            }
        }
    }

    /**
     *
     * @param position Int
     */
    private fun fragmentUnselected(position: Int) {
        getFragment(position)?.apply {
            if (hasLoadData) {
                onFragmentUnselected()
            }
        }
    }
}


interface TabFragmentListener {

    /**
     * onTabSelected
     */
    fun onFragmentSelected()

    /**
     * onTabUnselected
     */
    fun onFragmentUnselected()
}