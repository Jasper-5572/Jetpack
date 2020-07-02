package com.android.jasper.jetpack.page.personal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.R
import com.android.jasper.base.BaseFragment
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.data.UserInfo
import com.android.jasper.jetpack.page.personal.user_list.UserListActivity
import com.android.jasper.jetpack.view_model.TestViewModel
import kotlinx.android.synthetic.main.personal_fragment_personal.*
import java.util.*

/**
 *@author   Jasper
 *@create   2020/6/30 17:48
 *@describe
 *@update
 */
class PersonalFragment : BaseFragment<PersonalViewModel>() {
    companion object {
        fun newFragment(): PersonalFragment = PersonalFragment().apply {
            LogUtils.i("viewPager->PersonalFragment.apply()")
            lazy {
                viewModel = ViewModelProvider(this).get(PersonalViewModel::class.java)
            }

        }
    }
    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtils.i("viewPager->PersonalFragment.onCreateView()")
        return inflater.inflate(R.layout.personal_fragment_personal, null)
    }

    /**
     * 懒加载（页面显示才调用该方法）
     */
    override fun lazyLoad() {
        super.lazyLoad()
        initData()
        setListener()
    }


    /**
     * 初始化数据
     */
    private fun initData() {
        LogUtils.i("viewPager->PersonalFragment.initData()")
        viewModel?.userInfoData?.observe(this, Observer {
            it?.apply {
                tv_nick_name?.setString(this.nickName)
            }
        })

    }

    /**
     * 设置监听
     */
    private fun setListener() {
        tv_nick_name?.setOnClick {
            viewModel?.saveUserInfo(UserInfo(userId = UUID.randomUUID().toString()).apply {
                phone = "13365755735"
                nickName = "MrHu"
            })
        }

        tv_user_list?.setOnClick {
            startActivity(Intent(context, UserListActivity::class.java))
        }
    }

}