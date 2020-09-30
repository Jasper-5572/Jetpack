package com.android.jasper.jetpack.page.personal.user_list.add

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.jasper.base.ARounterPath
import com.android.jasper.base.BaseActivity
import com.android.jasper.base.getViewModel
import com.android.jasper.base.widget.ActivityViewWrapper
import com.android.jasper.base.widget.TitleWrapper
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.MainViewModel
import com.android.jasper.jetpack.R
import com.android.jasper.jetpack.data.AppDatabase
import kotlinx.android.synthetic.main.personal_activity_add_user.*

/**
 *@author   Jasper
 *@create   2020/7/3 17:20
 *@describe
 *@update
 */
@Route(path = ARounterPath.ADD_USER)
class AddUserActivity : BaseActivity<AddUserViewModel>() {
    override fun createViewModel(): AddUserViewModel? {
     return  getViewModel(AppDatabase.getInstance(this@AddUserActivity).userInfoDao())
//     return  getViewModel()
    }
    override fun createTitleWrapper(): ActivityViewWrapper? {
        return  TitleWrapper {
            tvTitle?.setString("添加用户")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_activity_add_user)
        initData()
        setListener()
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        layout_input_phone?.apply {
            error="请输入正确的手机号"
        }
    }

    /**
     * 设置监听
     */
    private fun setListener() {
        et_input_phone?.addTextChangedListener {

        }
        et_nick_name?.addTextChangedListener {

        }

        tv_ensure?.setOnClick {

        }
    }

}