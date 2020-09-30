package com.android.jasper.jetpack.page.personal.user_list.add

import androidx.lifecycle.MutableLiveData
import com.android.jasper.base.BaseViewModel
import com.android.jasper.framework.JasperFramework
import com.android.jasper.jetpack.data.AppDatabase
import com.android.jasper.jetpack.data.UserInfo
import com.android.jasper.jetpack.data.UserInfoDao

/**
 *@author   Jasper
 *@create   2020/7/3 17:47
 *@describe
 *@update
 */
class AddUserViewModel constructor(userInfoDao: UserInfoDao): BaseViewModel() {


    val createUserResult=MutableLiveData<Boolean>()
    private val userInfoDao: UserInfoDao? by lazy {
        JasperFramework.INSTANCE.application?.let {
            AppDatabase.getInstance(it).userInfoDao()
        }
    }


    fun createUser(userInfo: UserInfo){
        userInfoDao
    }



}