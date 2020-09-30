package com.android.jasper.jetpack.page.personal.user_list

import android.widget.TextView
import com.android.jasper.base.BaseAdapter
import com.android.jasper.framework.adapter.recycler_view.RvViewHolder
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.R
import com.android.jasper.jetpack.data.UserInfo

/**
 *@author   Jasper
 *@create   2020/7/3 10:09
 *@describe
 *@update
 */
class UserListAdapter: BaseAdapter<UserInfo>(R.layout.personal_item_user_list_adapter) {
    override fun onBindData(holder: RvViewHolder, data: UserInfo, position: Int) {
        holder.getView<TextView>(R.id.tv_phone)?.apply {
            setString(data.phone)
        }


        holder.getView<TextView>(R.id.tv_nick_name)?.apply {
            setString(data.nickName)
        }
    }
}