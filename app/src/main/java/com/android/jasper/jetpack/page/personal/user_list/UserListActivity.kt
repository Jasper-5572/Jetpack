package com.android.jasper.jetpack.page.personal.user_list

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.android.jasper.base.ARounterPath
import com.android.jasper.base.BaseActivity
import com.android.jasper.base.getViewModel
import com.android.jasper.base.widget.ActivityViewWrapper
import com.android.jasper.base.widget.TitleWrapper
import com.android.jasper.framework.ProxyFragmentManager
import com.android.jasper.framework.expansion.setStatusBarText
import com.android.jasper.framework.expansion.setStatusColor
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.R
import kotlinx.android.synthetic.main.personal_activity_user_list.*

/**
 *@author   Jasper
 *@create   2020/7/1 16:34
 *@describe 用户列表页面
 *@update
 */
@Route(path = ARounterPath.USER_LIST)
class UserListActivity : BaseActivity<UserListViewModel>() {
    override fun createViewModel(): UserListViewModel? {
        return getViewModel()
    }

    override fun createTitleWrapper(): ActivityViewWrapper? {
        return TitleWrapper {
            tvTitle?.setString("用户列表")
            toolTar?.setBackgroundColor(resources.getColor(R.color.colorWhite))
            supportActionBar?.setHomeAsUpIndicator(com.android.jasper.base.R.drawable.base_vector_black_back)
        }
    }

    private val listAdapter by lazy { UserListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_activity_user_list)
        setStatusColor(R.color.colorWhite)
        setStatusBarText(true)
        initData()
        setListener()
    }


    /**
     * 初始化数据
     */
    private fun initData() {
        smart_refresh?.apply {
            setEnableRefresh(true)
            setEnableLoadMore(false)
        }
        recycler_view?.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity)
            adapter = listAdapter
        }

        viewModel?.userListData?.observe(this, Observer {
            listAdapter.setList(it)
            listAdapter.notifyDataSetChanged()
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.personal_tool_bar_menu_user_list, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //back
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add -> {
                ARouter.getInstance().build(ARounterPath.ADD_USER).navigation(this)
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