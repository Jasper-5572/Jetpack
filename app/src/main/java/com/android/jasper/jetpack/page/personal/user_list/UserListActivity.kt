package com.android.jasper.jetpack.page.personal.user_list

import android.os.Bundle
import android.view.MenuItem
import com.android.jasper.base.BaseActivity
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.R
import kotlinx.android.synthetic.main.personal_activity_user_list.*

/**
 *@author   Jasper
 *@create   2020/7/1 16:34
 *@describe 用户列表页面
 *@update
 */
class UserListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_activity_user_list)
        initData()
        setListener()

    }


    /**
     * 初始化数据
     */
    private fun initData() {
        //ActionBar
        setSupportActionBar(tool_bar)
        supportActionBar?.apply {
//            setHomeAsUpIndicator(android.R.drawable.gobac)
            setDisplayHomeAsUpEnabled(true)
        }
        smart_refresh?.setEnableRefresh(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return  when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 设置监听
     */
    private fun setListener() {

    }
}