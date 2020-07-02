package com.android.jasper.framework.adapter.recycler_view

import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *@author   Jasper
 *@create   2020/5/21 15:08
 *@describe
 *@update
 */
interface RvItemListener {


    /**
     * @param <D>
     */
    interface OnItemClickListener<D> {
        /**
         * @param view
         * @param holder
         * @param data
         * @param position
         */
        fun onItemClick(view: View, holder: RecyclerView.ViewHolder, data: D, position: Int)
    }

    /**
     * @param <D>
    </D> */
    interface OnItemLongClickListener<D> {
        /**
         * @param view
         * @param holder
         * @param data
         * @param position
         *
         * @return
         */
        fun onItemLongClick(view: View, holder: RecyclerView.ViewHolder, data: D, position: Int): Boolean
    }
}