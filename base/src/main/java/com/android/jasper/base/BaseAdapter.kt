package com.android.jasper.base

import androidx.annotation.LayoutRes
import com.android.jasper.framework.adapter.recycler_view.NormalRvAdapter
import com.android.jasper.framework.adapter.recycler_view.RvTypeView
import com.android.jasper.framework.adapter.recycler_view.RvViewHolder

/**
 *@author   Jasper
 *@create   2020/7/2 16:14
 *@describe
 *@update
 */
abstract class BaseAdapter<D> constructor(
    @LayoutRes val dataLayoutRes: Int,
    //正在加载数据布局
    @LayoutRes loadingLayoutRes: Int = R.layout.item_rv_adapter_type_loading,
    //没有数据显示的布局
    @LayoutRes emptyLayoutRes: Int = R.layout.item_rv_adapter_type_empty,
    //加载数据失败的布局
    @LayoutRes errorLayoutRes: Int = R.layout.item_rv_adapter_type_error
) : NormalRvAdapter<D>(
    loadingLayoutRes, emptyLayoutRes, errorLayoutRes
) {

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
    protected open fun checkDefaultViewType(item: D, position: Int): Boolean =true
}