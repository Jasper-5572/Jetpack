package com.android.jasper.framework.adapter.recycler_view

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.android.jasper.framework.expansion.setString


/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe RecyclerView自定义ViewHolder
 *@update
 */
class RvViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mViewArray: SparseArray<View> by lazy { SparseArray<View>() }

    companion object {
        fun create(view: View): RvViewHolder =
            RvViewHolder(view)

        fun create(parentView: ViewGroup, @LayoutRes layoutId: Int): RvViewHolder =
            RvViewHolder(
                LayoutInflater.from(parentView.context).inflate(layoutId, parentView, false)
            )

    }

    /**
     * 通过viewId获取控件
     */
    fun <T : View> getView(@IdRes viewId: Int): T? {
        var view: View? = mViewArray.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViewArray.put(viewId, view)
        }
        @Suppress("UNCHECKED_CAST")
        return view as? T
    }

    /**
     * 为TextView 设置数据
     */
    fun setText(@IdRes viewId: Int, value: String?): RvViewHolder {
        getView<TextView>(viewId)?.setString(value)
        return this
    }

    /**
     * 为TextView 设置数据
     */
    fun setText(@IdRes viewId: Int, @StringRes valueId: Int): RvViewHolder {
        getView<TextView>(viewId)?.setString(valueId)
        return this
    }

    fun setVisibility(@IdRes viewId: Int, visibility: Int): RvViewHolder {
        getView<View>(viewId)?.visibility = visibility
        return this
    }
}