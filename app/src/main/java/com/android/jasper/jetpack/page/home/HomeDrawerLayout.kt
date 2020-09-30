package com.android.jasper.jetpack.page.home

import android.content.Context
import android.util.AttributeSet
import androidx.drawerlayout.widget.DrawerLayout

/**
 *@author   Jasper
 *@create   2020/7/23 09:39
 *@describe
 *@update
 */
class HomeDrawerLayout : DrawerLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mineWidthMeasureSpec =
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
        val mineHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        super.onMeasure(mineWidthMeasureSpec, mineHeightMeasureSpec)
    }
}