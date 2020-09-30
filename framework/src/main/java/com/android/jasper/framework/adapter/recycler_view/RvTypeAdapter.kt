package com.android.jasper.framework.adapter.recycler_view

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView


/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe RecyclerView通用型Adapter
 *@update
 */
open class RvTypeAdapter : RecyclerView.Adapter<RvViewHolder>() {
    protected val ktAdapterTool: RvAdapterTool by lazy { RvAdapterTool() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        ktAdapterTool.getItemView(viewType)?.apply {
            return getLayoutView()?.let {
                RvViewHolder.create(
                    it
                )
            } ?: RvViewHolder.create(
                parent,
                getLayoutId()
            )
        }
        throw RuntimeException("尚未注册对应viewType=$viewType 的ViewHolder")
    }

    override fun getItemCount(): Int {
        var itemCount = 0
        ktAdapterTool.mDataArray.apply {
            for (i in size() - 1 downTo 0) {
                itemCount += valueAt(i)?.size ?: 0
            }
        }
        return itemCount
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        ktAdapterTool.onConvert(holder, position)
    }

    override fun getItemViewType(position: Int): Int =ktAdapterTool.getItemViewType(position)

    /**
     * 注册ItemType
     * @param itemType Int itemType标识
     * @param itemView KtRvTypeView<D> ItemView构造器
     * @param canRepeated Boolean
     */
    fun <D> registerItemType(itemType: Int, itemView: RvTypeView<D>, canRepeated: Boolean = false) {
        if (itemType <= 0) {
            throw RuntimeException("itemType must >0")
        }
        ktAdapterTool.registerItemType(itemType, itemView, canRepeated)
    }


    /**
     * 设置对应的itemType的数据集合
     * @param itemType Int
     * @param typeList List<D>
     * @return RvTypeAdapter
     */
    fun <D> setList(itemType: Int, typeList: List<D>?): RvTypeAdapter {
        ktAdapterTool.mDataArray.put(itemType, typeList)
        return this
    }


    fun getList(itemType: Int): List<*>? =ktAdapterTool.mDataArray.get(itemType)

    /**
     * 获取完整的数据集合
     * @return SparseArrayCompat<List<*>>
     */
    protected fun getDataArray(): SparseArrayCompat<List<*>> =ktAdapterTool.mDataArray





    /**
     * 长按item监听
     * @param itemType Int itemType
     * @param longClickListener OnItemLongClickListener<D>
     */
    fun <D> setOnItemLongClickListener(
        itemType: Int,
        longClickListener: RvItemListener.OnItemLongClickListener<D>
    ) {
        ktAdapterTool.mItemLongClickListeners.put(itemType, longClickListener)
    }


    /**
     * 单击item监听
     * @param itemType Int itemType
     * @param clickListener OnItemClickListener<D> clickListener
     */
    fun <D> setOnItemClickListener(
        itemType: Int,
        clickListener: RvItemListener.OnItemClickListener<D>
    ) {
        ktAdapterTool.mItemClickListeners.put(itemType, clickListener)
    }
}