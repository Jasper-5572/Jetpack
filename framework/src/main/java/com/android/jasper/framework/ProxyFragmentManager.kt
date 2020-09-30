package com.android.jasper.framework

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 *@author   Jasper
 *@create   2020/7/7 12:09
 *@describe
 *@update
 */
class ProxyFragmentManager constructor(private val fragmentManager: FragmentManager) {
    /**
     * 记录上次触发时间
     */
    private var triggerLastTime: Long = 0L

    companion object {
        private const val defaultTriggerDelay = 300L
        fun of(fragment: Fragment): ProxyFragmentManager =
            ProxyFragmentManager(fragment.parentFragmentManager)

        fun of(activity: FragmentActivity): ProxyFragmentManager =
            ProxyFragmentManager(activity.supportFragmentManager)

        fun of(fragmentManager: FragmentManager): ProxyFragmentManager =
            ProxyFragmentManager(fragmentManager)
    }

    /**
     *
     * @param permissionSet Set<String>
     * @param permissionResult PermissionResult
     * @param triggerDelay Long 触发有效请求间隔时间
     */
    fun requestPermissionsResult(
        triggerDelay: Long = defaultTriggerDelay,
        permissionSet: Set<String>,
        permissionResult: PermissionResult
    ) {
        if (checkEnable(triggerDelay)) {
            ProxyFragment.getProxyFragment(fragmentManager)
                .requestPermissions(permissionSet.toTypedArray(), permissionResult)
        }

    }

    @JvmOverloads
    fun startActivityResult(
        intent: Intent,
        triggerDelay: Long = defaultTriggerDelay,
        block: (resultCode: Int, data: Intent?) -> Unit
    ) {
        if (checkEnable(triggerDelay)) {
            ProxyFragment.getProxyFragment(fragmentManager)
                .startForResult(intent, object : ActivityResult {
                    override fun onResult(resultCode: Int, data: Intent?) {
                        block(resultCode, data)
                    }
                })
        }
    }
    @JvmOverloads
    fun startActivityResult(
        aClass: Class<*>,
        triggerDelay: Long = defaultTriggerDelay,
        block: (resultCode: Int, data: Intent?) -> Unit
    ) {
        val intent = Intent(ProxyFragment.getProxyFragment(fragmentManager).context, aClass)
        startActivityResult(intent, triggerDelay, block)
    }


    /**
     * 检测是否可用跳转及请求
     * @return Boolean
     */
    private fun checkEnable(triggerDelay: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime >= triggerDelay + triggerLastTime) {
            triggerLastTime = currentTime
            true
        } else {
            false
        }
    }


}

open class ProxyFragment : Fragment() {
    private var requestCode = 1
    private val activityResultArray by lazy { SparseArray<ActivityResult>() }
    private val permissionResultArray by lazy { SparseArray<PermissionResult>() }


    companion object {
        private const val FRAGMENT_TAG = "JasperProxyPermission"

        /**
         *
         * @return PermissionFragment?
         */
        @Synchronized
        fun getProxyFragment(fragmentManager: FragmentManager): ProxyFragment {
            var proxyFragment: ProxyFragment? =
                fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? ProxyFragment
            if (proxyFragment == null) {
                proxyFragment = ProxyFragment().apply {
                    fragmentManager.beginTransaction()
                        .add(this, FRAGMENT_TAG)
                        // 防止跳转时 背景出现桌面
                        .hide(this)
                        .commitAllowingStateLoss()
                }
                fragmentManager.executePendingTransactions()
            }
            return proxyFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    /**
     * 防止 同时多个activity启动 造成request相同
     * @param intent   intent
     * @param callback 回调
     */
    @Synchronized
    fun startForResult(intent: Intent, callback: ActivityResult) {
        // 保证requestCode 每个都不同
        requestCode++
        activityResultArray.put(requestCode, callback)
        startActivityForResult(intent, requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultArray[requestCode]?.apply {
            onResult(resultCode, data)
        }
    }

    /**
     * Request permissions.
     *
     * @param permissions
     * @param callback
     */
    @Synchronized
    fun requestPermissions(permissions: Array<String>, callback: PermissionResult) {
        val requestPermissions = ArrayList<String>()
        permissions.forEach {
            if (!isGranted(it)) {
                requestPermissions.add(it)
            }
        }
        if (requestPermissions.isEmpty()) {
            callback.onGrant()
        } else {
            requestCode++
            permissionResultArray.put(requestCode, callback)
            this.requestPermissions(requestPermissions.toTypedArray(), requestCode)
        }
    }

    /**
     * 判断当前权限是否已经授权
     * @param permission String
     * @return Boolean
     */
    private fun isGranted(permission: String): Boolean {
        var result = false
        context?.let {
            result = if (it.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.M) {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                ) == PermissionChecker.PERMISSION_GRANTED
            } else {
                PermissionChecker.checkSelfPermission(
                    it,
                    permission
                ) == PermissionChecker.PERMISSION_GRANTED
            }
        }
        return result
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultArray[requestCode]?.let { permissionResult ->
            if (grantResults.isNotEmpty()) {
                this.onRequestPermissionsResult(permissions, grantResults, permissionResult)
            } else {
                permissionResult.onDenied(permissions)
            }
        }
        permissionResultArray.remove(requestCode)
    }

    private fun onRequestPermissionsResult(
        requestPermissionArray: Array<String>,
        grantResults: IntArray,
        permissionResult: PermissionResult
    ) {
        //拒绝权限
        val deniedPermissions = ArrayList<String>()
        //用户禁止的权限
        val rationalePermission = ArrayList<String>()
        //根据授权结果分别判断哪些没有授权
        for (i in requestPermissionArray.indices) {
            if (grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                deniedPermissions.add(requestPermissionArray[i])
            }
        }
        //没有拒绝授权的则认为档次请求权限全部授成功
        if (deniedPermissions.isEmpty()) {
            permissionResult.onGrant()
        } else {
            //判断哪些权限被用户禁止了
            deniedPermissions.forEach { deniedPermission ->
                if (!shouldShowRequestPermissionRationale(deniedPermission)) {
                    rationalePermission.add(deniedPermission)
                }
            }
            //有用户禁止的权限回调
            if (rationalePermission.isNotEmpty()) {
                permissionResult.onRationale(rationalePermission.toTypedArray())
            }

            permissionResult.onDenied(deniedPermissions.toTypedArray())
        }

    }
}

interface ActivityResult {
    /**
     *
     * @param resultCode Int
     * @param data Intent?
     */
    fun onResult(resultCode: Int, data: Intent?)
}


interface PermissionResult {
    /**
     * 全部授权
     */
    fun onGrant()

    /**
     * 用户禁止的权限
     * @param permissions 用户禁止的权限列表
     *
     */
    fun onRationale(permissions: Array<String>) {}

    /**
     * 拒绝授权回调
     * @param permissions 拒绝授权的列表
     *
     */
    fun onDenied(permissions: Array<String>) {}
}