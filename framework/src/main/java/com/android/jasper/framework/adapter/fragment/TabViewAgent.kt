package com.android.jasper.framework.adapter.fragment

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout

/**
 *@author   Mr.Hu(Jc) OrgVTrade
 *@create   2019-11-09 14:15
 *@describe
 *@update
 */
open class TabViewAgent(val context: Context,
                        val tabLayout: TabLayout) {

    init {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    changeTabUnselected(it)
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    changeTabSelect(it)
                }
            }


        })
    }


    internal fun prepare(adapter: FragmentAdapter<*>) {
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.customView = createCustomView(i, adapter)
        }
    }

    open fun createCustomView(position: Int, adapter: FragmentAdapter<*>): View {
        return DefaultView(adapter.getPageTitle(position))
    }


    /**
     * 改变TabLayout的View到选中状态
     * 使用属性动画改编Tab中View的状态
     */
    open fun changeTabSelect(tab: TabLayout.Tab) {
//        t.customView?.let {
//            val anim = ObjectAnimator
//                    .ofFloat(it, "", 1.0f, 1.1f)
//                    .setDuration(150)
//            anim.start()
//            anim.addUpdateListener { animation ->
//                (animation.animatedValue as? Float)?.run {
//                    it.alpha = 0.9f + (this - 1f) * (0.5f / 0.1f)
//                    it.scaleX = this
//                    it.scaleY = this
//                }
//
//            }
//        }
    }

    /**
     * 改变TabLayout的View到未选中状态
     */
    open fun changeTabUnselected(tab: TabLayout.Tab) {
//        t.customView?.let {
//            val anim = ObjectAnimator
//                    .ofFloat(it, "", 1.0f, 0.9f)
//                    .setDuration(150)
//            anim.start()
//            anim.addUpdateListener { animation ->
//                (animation.animatedValue as? Float)?.run {
////                    val cVal = animation.animatedValue as? Float
//                    it.alpha = 1f - (1f - this) * (0.5f / 0.1f)
//                    it.scaleX = this
//                    it.scaleY = this
//                }
//
//            }
//        }

    }

    internal inner class DefaultView(title: CharSequence?) : LinearLayout(context) {
        private val textView: TextView by lazy { TextView(context) }

        init {
            textView.text = title
            textView.setTextColor(tabLayout.tabTextColors)
            gravity = Gravity.CENTER
            orientation = VERTICAL
            addView(textView)

        }

    }

}

