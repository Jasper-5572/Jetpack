package com.android.jasper.framework.adapter.recycler_view

import android.view.View
import androidx.annotation.LayoutRes
import com.android.jasper.framework.expansion.setOnClick


/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe RecyclerView的Item对应View构造器
 *@update
 */
abstract class RvTypeView<D> {
    /**
     * item布局资源id
     *
     * @return 布局资源id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun getLayoutView(): View? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    open fun onConvert(
        holder: RvViewHolder,
        data: Any,
        position: Int,
        onItemClickListener: RvItemListener.OnItemClickListener<*>?,
        onItemLongClickListener: RvItemListener.OnItemLongClickListener<*>?
    ) {
        (data as? D)?.let { d ->
            (onItemClickListener as? RvItemListener.OnItemClickListener<D>)?.let {
                holder.itemView.setOnClick { v -> it.onItemClick(v, holder, d, position) }
            }
            (onItemLongClickListener as? RvItemListener.OnItemLongClickListener<D>)?.let {
                holder.itemView.setOnLongClickListener { v ->
                    it.onItemLongClick(v, holder, d, position)
                }
            }
            onConvertData(holder, d, position)
        }
    }

    /**
     * 布局赋值
     * @param holder RvViewHolder
     * @param data D
     * @param position Int
     */
    abstract fun onConvertData(holder: RvViewHolder, data: D, position: Int)

    internal fun checkType(data: Any?, position: Int): Boolean {
        @Suppress("UNCHECKED_CAST")
        val d: D? = data as? D
        d?.let {
            return checkViewType(it, position)
        }
        return false

    }

    /**
     * 检测数据类型type是否一致,如果数据格式相同 布局不同可以通过此方法区分
     * @param item D
     * @param position Int
     * @return Boolean
     */
    open fun checkViewType(item: D, position: Int): Boolean {
        return true
    }
}