package com.android.jasper.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.jasper.base.widget.ActivityViewWrapper
import kotlinx.android.synthetic.main.base_activity_layout.*

/**
 *@author   Jasper
 *@create   2020/5/18 17:03
 *@describe
 *@update
 */
open class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {
    /**
     * 加载对话框
     */
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }


   private val viewWrapper: ActivityViewWrapper? by lazy {
       createTitleWrapper()?.apply {
           lifecycle.addObserver(this)
       }
   }


    /**
     * viewModel 通过[createViewModel]
     */
    val viewModel: VM? by lazy {
        createViewModel()?.apply {
            lifecycle.addObserver(this)
            //加载对话框
            loadingLiveData.observe(this@BaseActivity, Observer { showLoading ->
                if (showLoading) {
                    loadingDialog.onShow(this@BaseActivity)
                } else {
                    loadingDialog.dismiss()
                }
            })
            //
            toastStringLiveData.observe(this@BaseActivity, Observer {
                Toast.makeText(this@BaseActivity, it, Toast.LENGTH_LONG).show()
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    @SuppressLint("InflateParams")
    override fun setContentView(layoutResID: Int) {
        viewWrapper?.let {
            super.setContentView(R.layout.base_activity_layout)
            ll_parent?.let { viewParent ->
                viewParent.removeAllViews()
                LayoutInflater.from(this).inflate(layoutResID, viewParent)
                viewParent.addView(LayoutInflater.from(this).inflate(it.getLayoutResource(), null), 0)
                it.initView(this@BaseActivity)
            }
        } ?: super.setContentView(layoutResID)
    }

    @SuppressLint("InflateParams")
    override fun setContentView(view: View?) {
        viewWrapper?.let {
            super.setContentView(R.layout.base_activity_layout)
            ll_parent?.let { viewParent ->
                view?.apply {
                    viewParent.removeAllViews()
                    viewParent.addView(this)
                }
                viewParent.addView(LayoutInflater.from(this).inflate(it.getLayoutResource(), null), 0)
                it.initView(this@BaseActivity)
            }
        } ?: super.setContentView(view)

    }

    /**
     * 创建ViewModel
     * @return VM?
     */
    protected open fun createViewModel(): VM? = null

    /**
     * 创建
     * @return ActivityViewWrapper?
     */
    protected open fun createTitleWrapper():ActivityViewWrapper?=null

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //back
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

