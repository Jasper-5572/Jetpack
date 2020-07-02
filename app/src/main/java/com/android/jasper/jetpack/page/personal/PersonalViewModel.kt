package com.android.jasper.jetpack.page.personal

import androidx.lifecycle.MutableLiveData
import com.android.jasper.framework.JasperFramework
import com.android.jasper.base.BaseViewModel
import com.android.jasper.jetpack.data.AppDatabase
import com.android.jasper.jetpack.data.UserInfo
import com.android.jasper.jetpack.data.UserInfoDao
import kotlinx.coroutines.*

/**
 *@author   Jasper
 *@create   2020/7/1 13:45
 *@describe
 *@update
 */
class PersonalViewModel : BaseViewModel() {
    private val userInfoDao: UserInfoDao? by lazy {
        JasperFramework.INSTANCE.application?.let {
            AppDatabase.getInstance(it).userInfoDao()
        }
    }
    val userInfoData = MutableLiveData<UserInfo?>()

    override fun lazyLoad() {
        super.lazyLoad()
        dataLoading.value = true
        GlobalScope.launch (Dispatchers.IO){
            val userInfo = userInfoDao?.getUserInfoByPhone("13365755735")
            withContext(Dispatchers.Main){
                userInfoData.value = userInfo
                dataLoading.value = false
            }
        }
    }


    fun saveUserInfo(userInfo: UserInfo) {
        dataLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            userInfoDao?.insert(userInfo)
            withContext(Dispatchers.Main){
                userInfoData.value = userInfo
                dataLoading.value = false
            }
        }
    }

}