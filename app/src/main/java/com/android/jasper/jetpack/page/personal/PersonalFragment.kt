package com.android.jasper.jetpack.page.personal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.android.jasper.base.ARounterPath
import com.android.jasper.framework.expansion.setOnClick
import com.android.jasper.framework.expansion.setString
import com.android.jasper.jetpack.R
import com.android.jasper.base.BaseFragment
import com.android.jasper.framework.util.LogUtils
import com.android.jasper.jetpack.base.DefaultNavigationCallback
import com.android.jasper.jetpack.data.UserInfo
import kotlinx.android.synthetic.main.personal_fragment_personal.*

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
        }
    }
    override fun createViewModel(): PersonalViewModel? {
        return ViewModelProvider(this).get(PersonalViewModel::class.java)
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
            viewModel?.saveUserInfo(UserInfo().apply {
                phone = "13365755735"
                nickName = "MrHu"
            })
        }

        tv_user_list?.setOnClick {
            activity?.apply {
//              val postcard = ARouter.getInstance().build(ARounterPath.USER_LIST)
//                postcard?.destination
                ARouter.getInstance().build(ARounterPath.USER_LIST)
                    .setTag("login")
                    .navigation(this, object : DefaultNavigationCallback {
                        override fun onInterrupt(postcard: Postcard?) {
                            postcard?.let {
                                val path = it.path
                                val bundle = it.extras
                                // 需要调转到登录页面，把参数跟被登录拦截下来的路径传递给登录页面，登录成功后再进行跳转被拦截的页面
//                                ARouter.getInstance().build(ConfigConstants.LOGIN_PATH)
//                                    .with(bundle)
//                                    .withString(ConfigConstants.PATH, path)
//                                    .navigation()
                            }

                        }
                    })
            }
        }
    }



}