package com.android.jasper.jetpack.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *@author   Jasper
 *@create   2020/5/18 17:43
 *@describe
 *@update
 */
class TestViewModel : ViewModel() {
    private val livaData = MutableLiveData<String>()

    init {
        livaData.value = "anriku"
    }

    fun getLiveData(): MutableLiveData<String> = livaData
}