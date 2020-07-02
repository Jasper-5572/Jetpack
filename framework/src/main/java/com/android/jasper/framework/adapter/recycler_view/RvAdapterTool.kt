package com.android.jasper.framework.adapter.recycler_view

import androidx.collection.SparseArrayCompat


/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe
 *@update
 */
class RvAdapterTool {
    /**
     * 外层不同itemType,内层为注册相同的itemType,交给上层去区分
     * [RvTypeView.checkViewType]
     */
    internal val mItemTypeArray: SparseArrayCompat<SparseArrayCompat<RvTypeView<*>>> by lazy { SparseArrayCompat<SparseArrayCompat<RvTypeView<*>>>() }

    /**
     * 数据map,key为itemType
     */
    internal val mDataArray: SparseArrayCompat<List<*>?> by lazy { SparseArrayCompat<List<*>?>() }

    /**
     * item单击监听集合 key为itemType
     */
    internal val mItemClickListeners: SparseArrayCompat<RvItemListener.OnItemClickListener<*>> by lazy { SparseArrayCompat<RvItemListener.OnItemClickListener<*>>() }

    /**
     * item长按监听集合 key为itemType
     */
    internal val mItemLongClickListeners: SparseArrayCompat<RvItemListener.OnItemLongClickListener<*>> by lazy { SparseArrayCompat<RvItemListener.OnItemLongClickListener<*>>() }

    /**
     * 临时保存itemType对应布局数据
     */
    private val itemTypeArrayList: SparseArrayCompat<RvTypeView<*>> by lazy { SparseArrayCompat<RvTypeView<*>>() }


    /**
     *
     * @param itemType Int itemType
     * @param itemView RvTypeView<D> itemView
     * @param canRepeated Boolean 是否允许重复注册同一类型itemType
     */
    fun <D> registerItemType(itemType: Int, itemView: RvTypeView<D>, canRepeated: Boolean) {
        when {
            mItemTypeArray.indexOfKey(itemType) < 0 -> {
                SparseArrayCompat<RvTypeView<*>>().let {
                    addItemViewType(it, itemView, itemType)
                    mItemTypeArray.put(itemType, it)
                }
            }
            canRepeated -> {
                addItemViewType(mItemTypeArray.get(itemType), itemView, itemType)
            }
            else -> {
                throw RuntimeException("重复注册itemType=$itemType")
            }
        }
    }

    fun getItemView(itemType: Int): RvTypeView<*>? {
        return itemTypeArrayList.get(itemType)
    }

    /**
     * 注册同一类型itemType的时候添加到一起 一般出现这种情况是因为数据类型相同,需要展示不同的样式
     * @param typeArray SparseArrayCompat<RvTypeView<*>>? 同一类型itemType itemView集合
     * @param itemView RvTypeView<*>? itemView
     * @param itemType Int itemType
     */
    private fun addItemViewType(
        typeArray: SparseArrayCompat<RvTypeView<*>>?,
        itemView: RvTypeView<*>?,
        itemType: Int
    ) {
        itemView?.let {
            typeArray?.put(itemType * 10 + typeArray.size(), it)
        }

    }


    /**
     * 如果注册了同一类型itemType的多itemView,具体区分要根据上层用户itemView.checkType(data, position)区分
     * @param typeArray SparseArrayCompat<RvTypeView<*>> 同一类型itemType itemView集合
     * @param data Any? 当前数据
     * @param position Int index
     * @return Int 返回itemType
     */
    private fun getItemViewType(
        typeArray: SparseArrayCompat<RvTypeView<*>>,
        data: Any?,
        position: Int
    ): Int {
        val typeArraySize = typeArray.size()
        for (index in typeArraySize - 1 downTo 0) {
            val itemView = typeArray.valueAt(index)
            if (itemView.checkType(data, position)) {
                return typeArray.keyAt(index).apply {
                    itemTypeArrayList.put(this, typeArray.get(this))
                }
            }
        }
        throw IllegalArgumentException("No KtRvTypeView added that matches position=$position in data source")

    }

    /**
     * 获取item的viewType
     *
     * @param position 下标
     * @return viewType
     */
    fun getItemViewType(position: Int): Int {
        val dataListCount = mDataArray.size()
        var itemCount = 0
        for (i in dataListCount - 1 downTo 0) {
            mDataArray.valueAt(i)?.let {list->
                itemCount += list.size
                //如果当前下标在当前过滤的所有list长度以内
                if (position < itemCount) {
                    val itemType = mDataArray.keyAt(i)
                    mItemTypeArray.get(itemType)?.let {
                        if (it.size() <= 1) {
                            //如果添加的itemType数据只有一种类型，直接返回
                            itemTypeArrayList.put(itemType, it.get(it.keyAt(0)))
                            return itemType
                        }
                        return getItemViewType(it, list[position - (itemCount - list.size)], position)
                    }
                }
            }
        }
        throw IllegalArgumentException("No KtRvTypeView added that matches position=$position in data source")
    }

    fun onConvert(holder: RvViewHolder, position: Int) {
        val dataListCount = mDataArray.size()
        var itemCount = 0
        for (i in dataListCount - 1 downTo 0) {
            mDataArray.valueAt(i)?.let { list->
                itemCount += list.size
                //如果当前下标在当前过滤的所有list长度以内
                if (position < itemCount) {
                    val itemType = mDataArray.keyAt(i)
                    mItemTypeArray.get(itemType)?.let {
                        onConvert(
                            it,
                            itemType,
                            list[position - (itemCount - list.size)],
                            position,
                            holder
                        )
                        return
                    }
                }
            }
        }
        throw IllegalArgumentException("No RvTypeView added that matches position=$position in data source")
    }

    private fun onConvert(
        typeArray: SparseArrayCompat<RvTypeView<*>>,
        itemType: Int,
        data: Any?,
        position: Int,
        holder: RvViewHolder
    ) {
        val typeArraySize = typeArray.size()
        for (index in typeArraySize - 1 downTo 0) {
            val itemView = typeArray.valueAt(index)
            if (itemView.checkType(data, position)) {
                data?.let {
                    itemView.onConvert(
                        holder,
                        it,
                        position,
                        mItemClickListeners.get(itemType),
                        mItemLongClickListeners.get(itemType)
                    )
                }
                return
            }
        }
        throw IllegalArgumentException("No RvTypeView added that matches position=$position in data source")
    }
}