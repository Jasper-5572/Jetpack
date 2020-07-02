package com.android.jasper.jetpack.view_model

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.jasper.base.BaseActivity

/**
 *@author   Jasper
 *@create   2020/4/10 15:16
 *@describe
 *@update
 */
class ViewModelActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewModelProvider(this)
            .get(TestViewModel::class.java)
            .getLiveData()
            .observe(this,
                Observer<String> {

                })


    }
}