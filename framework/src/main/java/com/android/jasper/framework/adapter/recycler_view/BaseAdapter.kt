package com.android.jasper.framework.adapter.recycler_view

import android.view.View
import androidx.annotation.LayoutRes
import com.android.jasper.framework.JasperConfigurationManager

/**
 *@author   Jasper
 *@create   2020/7/27 14:43
 *@describe
 *@update
 */
abstract class BaseAdapter<D>(
        @LayoutRes val dataLayoutRes: Int,
        //正在加载数据布局
        @LayoutRes loadingLayoutRes: Int = JasperConfigurationManager.INSTANCE.jasperConfiguration?.adapterLayoutRes?.loadingLayoutRes
                ?: View.NO_ID,
        //没有数据显示的布局
        @LayoutRes emptyLayoutRes: Int = JasperConfigurationManager.INSTANCE.jasperConfiguration?.adapterLayoutRes?.emptyLayoutRes
                ?: View.NO_ID,
        //加载数据失败的布局
        @LayoutRes errorLayoutRes: Int = JasperConfigurationManager.INSTANCE.jasperConfiguration?.adapterLayoutRes?.errorLayoutRes
                ?: View.NO_ID) : NormalRvAdapter<D>(loadingLayoutRes, emptyLayoutRes, errorLayoutRes) {
    init {
        addDataItemType(itemTypeView = object : RvTypeView<D>() {

            override fun onConvertData(holder: RvViewHolder, data: D, position: Int) = onBindData(holder, data, position)

            override fun getLayoutId(): Int = dataLayoutRes

            override fun checkViewType(item: D, position: Int): Boolean = checkDefaultViewType(item, position)
        })
    }
    /**
     * 绑定数据
     *
     * @param holder   ViewHolder
     * @param data     数据
     * @param position position
     */
    abstract fun onBindData(holder: RvViewHolder, data: D, position: Int)

    /**
     * 检测默认
     * @param item
     * @param position
     * @return
     */
    protected open fun checkDefaultViewType(item: D, position: Int): Boolean {
        return true
    }
}

data class AdapterLayoutResBean(
        //正在加载数据布局
        @LayoutRes val loadingLayoutRes: Int = View.NO_ID,
        //没有数据显示的布局
        @LayoutRes val emptyLayoutRes: Int = View.NO_ID,
        //加载数据失败的布局
        @LayoutRes val errorLayoutRes: Int = View.NO_ID
)