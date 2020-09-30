package com.android.jasper.framework.adapter.recycler_view

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 *@author   Jasper
 *@create   2020/6/11 14:51
 *@describe 同种数据类型通用型RecyclerView的Adapter
 *@update
 */
open class NormalRvAdapter<D> constructor(
        //正在加载数据布局
        @LayoutRes val loadingLayoutRes: Int = View.NO_ID,
        //没有数据显示的布局
        @LayoutRes val emptyLayoutRes: Int = View.NO_ID,
        //加载数据失败的布局
        @LayoutRes val errorLayoutRes: Int = View.NO_ID
) : RvTypeAdapter() {
    /**
     * 获取数据长度 不包含异常的数据
     */
    val dataSize: Int get() = mList?.size ?: 0
    open var mList: MutableList<D>? = null
        protected set

    companion object {
        /**
         * 正在加载itemType
         */
        const val LOADING_ITEM_TYPE: Int = 1000

        /**
         * 没有数据itemType
         */
        const val EMPTY_ITEM_TYPE: Int = 1001

        /**
         * 加载数据失败itemType
         */
        const val ERROR_ITEM_TYPE: Int = 1002

        /**
         * 数据itemType
         */
        const val DATA_ITEM_TYPE: Int = 1
    }


    init {
        showLoading()
    }

    /**
     * 设置数据
     *
     * @param list 数据list
     * @return 当前对象
     */
    open fun setList(list: MutableList<D>?): NormalRvAdapter<D> {
        clearOtherItemType()
        mList = list
        super.setList(DATA_ITEM_TYPE, list)
        if (dataSize == 0) {
            showEmpty()
        }
        return this
    }

    /**
     * 显示正在加载
     */
    open fun showLoading() {
        clearOtherItemType()
        if (loadingLayoutRes != View.NO_ID) {
            registerItemType(LOADING_ITEM_TYPE, object : RvTypeView<String>() {
                override fun onConvertData(holder: RvViewHolder, data: String, position: Int) {}

                override fun getLayoutId(): Int = loadingLayoutRes
            })
            getDataArray().clear()
            getDataArray().put(LOADING_ITEM_TYPE, listOf("加载中...."))
            notifyDataSetChanged()
        }
    }

    /**
     * 显示正在加载
     */
    open fun showError(whenNoData: Boolean = true) {
        val show = if (whenNoData) dataSize == 0 else true
        if (show) {
            clearOtherItemType()
            if (loadingLayoutRes != View.NO_ID) {
                registerItemType(ERROR_ITEM_TYPE, object : RvTypeView<String>() {
                    override fun onConvertData(holder: RvViewHolder, data: String, position: Int) = onBindError(holder)
                    override fun getLayoutId(): Int = errorLayoutRes

                })
                getDataArray().clear()
                getDataArray().put(ERROR_ITEM_TYPE, listOf("加载失败"))
                notifyDataSetChanged()
            }
        }

    }

    open fun onBindError(holder: RvViewHolder) {}

    /**
     * 显示暂无数据
     */
    open fun showEmpty() {
        clearOtherItemType()
        if (emptyLayoutRes != View.NO_ID) {
            registerItemType(EMPTY_ITEM_TYPE, object : RvTypeView<String>() {
                override fun onConvertData(holder: RvViewHolder, data: String, position: Int) {}

                override fun getLayoutId(): Int = emptyLayoutRes
            })
            getDataArray().clear()
            getDataArray().put(EMPTY_ITEM_TYPE, listOf("暂无数据"))
            notifyDataSetChanged()
        }


    }

    /**
     * 添加数据类型
     * @param itemTypeView RvTypeView<D>
     */
    fun addDataItemType(itemTypeView: RvTypeView<D>) {
        registerItemType(DATA_ITEM_TYPE, itemTypeView, true)
    }


    /**
     * 原数据末尾追加数据
     * @param list MutableList<D>
     * @return NormalRvAdapter<D>
     */
    open fun addList(list: MutableList<D>): NormalRvAdapter<D> {
        mList?.addAll(list) ?: setList(list)
        return this
    }

    /**
     * 移除异常状态的数据
     */
    private fun clearOtherItemType() {
        ktAdapterTool.mDataArray.remove(EMPTY_ITEM_TYPE)
        ktAdapterTool.mItemTypeArray.remove(EMPTY_ITEM_TYPE)
        ktAdapterTool.mDataArray.remove(ERROR_ITEM_TYPE)
        ktAdapterTool.mItemTypeArray.remove(ERROR_ITEM_TYPE)
        ktAdapterTool.mDataArray.remove(LOADING_ITEM_TYPE)
        ktAdapterTool.mItemTypeArray.remove(LOADING_ITEM_TYPE)
    }

    /**
     * 点击错误item监听
     * @param listener Function0<Unit>
     */
    open fun setClickErrorListener(listener: (() -> Unit)) {
        if (errorLayoutRes != View.NO_ID) {
            setOnItemClickListener(ERROR_ITEM_TYPE, object :
                RvItemListener.OnItemClickListener<D> {
                override fun onItemClick(
                        view: View, holder: RecyclerView.ViewHolder, data: D, position: Int
                ) {
                    showLoading()
                    listener.invoke()
                }

            })
        }
    }

    /**
     * 点击暂无数据的item监听
     * @param listener Function0<Unit>
     */
    open fun setClickEmptyListener(listener: (() -> Unit)) {
        setOnItemClickListener(EMPTY_ITEM_TYPE, object :
            RvItemListener.OnItemClickListener<D> {
            override fun onItemClick(
                    view: View, holder: RecyclerView.ViewHolder, data: D, position: Int
            ) {
                listener.invoke()
            }

        })
    }

    /**
     * 此处只是添加正常数据的监听
     * @param listener Function4<[@kotlin.ParameterName] View, [@kotlin.ParameterName] ViewHolder, [@kotlin.ParameterName] D, [@kotlin.ParameterName] Int, Unit>
     */
    open fun setOnItemClickListener(listener: ((view: View, holder: RecyclerView.ViewHolder, data: D, position: Int) -> Unit)) {
        setOnItemClickListener(DATA_ITEM_TYPE, object :
            RvItemListener.OnItemClickListener<D> {
            override fun onItemClick(
                    view: View, holder: RecyclerView.ViewHolder, data: D, position: Int
            ) {
                listener.invoke(view, holder, data, position)
            }

        })
    }

    /**
     * 长按监听
     * @param listener Function4<[@kotlin.ParameterName] View, [@kotlin.ParameterName] ViewHolder, [@kotlin.ParameterName] D, [@kotlin.ParameterName] Int, Boolean>
     */
    open fun setOnItemLongClickListener(listener: ((view: View, holder: RecyclerView.ViewHolder, data: D, position: Int) -> Boolean)) {
        setOnItemLongClickListener(
                DATA_ITEM_TYPE,
                object :
                    RvItemListener.OnItemLongClickListener<D> {
                    override fun onItemLongClick(
                            view: View,
                            holder: RecyclerView.ViewHolder,
                            data: D,
                            position: Int
                    ): Boolean {
                        return listener.invoke(view, holder, data, position)
                    }

                })
    }
}