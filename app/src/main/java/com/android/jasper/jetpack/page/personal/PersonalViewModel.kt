package com.android.jasper.jetpack.page.personal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.jasper.base.BaseViewModel
import com.android.jasper.base.launch
import com.android.jasper.framework.JasperFramework
import com.android.jasper.jetpack.data.AppDatabase
import com.android.jasper.jetpack.data.UserInfo
import com.android.jasper.jetpack.data.UserInfoDao
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.lang.RuntimeException

/**
 *@author   Jasper
 *@create   2020/7/1 13:45
 *@describe
 *@update
 */
class PersonalViewModel : BaseViewModel() {
    private val userInfoDao: UserInfoDao? by lazy {
        JasperFramework.INSTANCE.application.let {
            AppDatabase.getInstance(it).userInfoDao()
        }
    }
    val userInfoData = MutableLiveData<UserInfo?>()

    @Throws(RuntimeException::class)
    private suspend fun loadUserInfo(): UserInfo? {
        throw RuntimeException("")
//        return userInfoDao?.getUserInfoByPhone("13365755735")
    }

    override fun lazyLoad() {
        super.lazyLoad()

        launch(onError = {
            toastThrowableLiveData.value = it
        }, onFinally = {
            loadingLiveData.value = false
        }) {
            loadingLiveData.value = true
            val userInfo = async(Dispatchers.IO) {
                delay(5000L)
                loadUserInfo()
            }
            userInfoData.value = userInfo.await()
        }
//        viewModelScope.launch {
//            loadingLiveData.value = true
//            val userInfo = async(Dispatchers.IO) {
//                delay(5000L)
//                loadUserInfo()
//            }
//            userInfoData.value = userInfo.await()
//            loadingLiveData.value = false
//
////
////            loadingLiveData.value = true
////            delay(5000L)
////            val userInfo = userInfoDao?.getUserInfoByPhone("13365755735")
////            userInfoData.value = userInfo
////            loadingLiveData.value = false
//
//        }
    }

    fun saveUserInfo(userInfo: UserInfo) {
        loadingLiveData.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userInfoDao?.insert(userInfo)
                delay(5000L)
            }
            userInfoData.value = userInfo
            loadingLiveData.value = false
        }
    }

}