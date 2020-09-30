package com.android.jasper.jetpack.page.personal.user_list

import androidx.lifecycle.MutableLiveData
import com.android.jasper.base.BaseViewModel
import com.android.jasper.framework.JasperFramework
import com.android.jasper.jetpack.data.AppDatabase
import com.android.jasper.jetpack.data.UserInfo
import com.android.jasper.jetpack.data.UserInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *@author   Jasper
 *@create   2020/7/3 09:52
 *@describe
 *@update
 */
class UserListViewModel : BaseViewModel() {
    private val userInfoDao: UserInfoDao? by lazy {
        JasperFramework.INSTANCE.application?.let {
            AppDatabase.getInstance(it).userInfoDao()
        }
    }
    val userListData = MutableLiveData<MutableList<UserInfo>?>()

    /**
     * 懒加载（页面显示才调用该方法）
     */
    override fun lazyLoad() {
        super.lazyLoad()
        loadingLiveData.value = true
        GlobalScope.launch(Dispatchers.IO) {
            val userList = userInfoDao?.getUserInfoList()
            withContext(Dispatchers.Main) {
                userListData.value = userList
                loadingLiveData.value = false
            }
        }
    }


}