package com.android.jasper.jetpack

import androidx.lifecycle.MutableLiveData
import com.android.jasper.base.BaseViewModel

/**
 *@author   Jasper
 *@create   2020/7/2 11:12
 *@describe
 *@update
 */
class MainViewModel : BaseViewModel() {

    val showDrawLayout by lazy { MutableLiveData<Boolean>() }
}