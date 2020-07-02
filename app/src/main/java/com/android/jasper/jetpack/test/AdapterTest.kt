package com.android.jasper.jetpack.test

import com.android.jasper.framework.adapter.recycler_view.NormalRvAdapter

/**
 *@author   Jasper
 *@create   2020/6/11 15:21
 *@describe
 *@update
 */
class TestAdapter: NormalRvAdapter<String>() {


}


class Test{


    fun test(){
        TestAdapter().setOnItemClickListener { _, _, _, _ ->

        }

    }
}