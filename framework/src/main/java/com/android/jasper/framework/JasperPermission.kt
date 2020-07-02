package com.android.jasper.framework

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 *@author   Mr.Hu(Jc) OrgVTrade
 *@create   2019-11-09 15:44
 *@describe
 *@update
 */
class JasperPermission private constructor(
    val fragment: Fragment? = null,
    val activity: FragmentActivity? = null
) {
    private val permissionSet by lazy { hashSetOf<String>() }
    private var listener: IPermissionListener? = null

    companion object {
        private const val REQUEST_PERMISSION: Int = 1828
        private const val FRAGMENT_TAG = "JasperPermission"
        fun with(fragment: Fragment): JasperPermission {
            return JasperPermission(fragment = fragment)
        }

        fun with(activity: FragmentActivity): JasperPermission {
            return JasperPermission(activity = activity)
        }
    }

    /**
     * 添加需要申请的权限，多个权限分别添加
     *
     * @param permission 需要申请的权限
     * @return builder
     */
    fun permission(permission: String): JasperPermission {
        permissionSet.add(permission)
        return this
    }

    /**
     * 添加监听
     *
     * @param listener 回调监听
     * @return
     */
    fun listener(listener: IPermissionListener): JasperPermission {
        this.listener = listener
        return this
    }

    /**
     * Request.
     */
    fun request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionFragment()?.requestPermissions(permissionSet.toList(), listener)
        } else {
            listener?.onGrant()
        }

    }

    /**
     *
     * @return PermissionFragment?
     */
    private fun getPermissionFragment(): PermissionFragment? {
        var permissionFragment: PermissionFragment? = null
        if (activity != null) {
            val fragmentManager = activity.supportFragmentManager
            permissionFragment =
                fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? PermissionFragment
            if (permissionFragment == null) {
                permissionFragment = PermissionFragment().apply {
                    fragmentManager.beginTransaction().add(this, FRAGMENT_TAG).commit()
                    fragmentManager.executePendingTransactions()
                }
            }
        } else if (fragment != null) {
            val fragmentManager = fragment.childFragmentManager
            permissionFragment =
                fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? PermissionFragment
            if (permissionFragment == null) {
                permissionFragment = PermissionFragment().apply {
                    fragmentManager.beginTransaction().add(this, FRAGMENT_TAG).commit()
                    fragmentManager.executePendingTransactions()
                }

            }
        }
        return permissionFragment
    }

    class PermissionFragment : Fragment() {
        private var listener: IPermissionListener? = null

        /**
         * Request permissions.
         *
         * @param permissions
         * the permissions
         * @param listener
         * the listener
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        fun requestPermissions(permissions: List<String>, listener: IPermissionListener?) {
            this.listener = listener
            val requestPermissions = ArrayList<String>()
            for (s in permissions) {
                if (!isGranted(s)) {
                    requestPermissions.add(s)
                }
            }
            if (requestPermissions.isEmpty()) {
                listener?.onGrant()
            } else {
                this.requestPermissions(requestPermissions.toTypedArray(), REQUEST_PERMISSION)
            }


        }

        private fun isGranted(permission: String): Boolean {
            var result = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            }
            return result
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_PERMISSION) {
                if (grantResults.isNotEmpty()) {
                    this.onRequestPermissionsResult(permissions, grantResults)
                } else {
                    listener?.onDenied(permissions)
                }

            }

        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
            val deniedPermissions = ArrayList<String>()
            val rationalePermission = ArrayList<String>()
            for (i in permissions.indices) {
                if (grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }
            if (deniedPermissions.isEmpty()) {
                this.listener?.onGrant()
            } else {
                for (d in deniedPermissions) {
                    if (!shouldShowRequestPermissionRationale(d)) {
                        rationalePermission.add(d)
                    }
                }
                if (rationalePermission.isNotEmpty()) {
                    this.listener?.onRationale(rationalePermission.toTypedArray())
                }

                this.listener?.onDenied(deniedPermissions.toTypedArray())
            }

        }


    }

    /**
     * 权限申请回调
     */
    interface IPermissionListener {

        /**
         * 全部授权
         */
        fun onGrant()

        /**
         * 部分授权回调
         *
         * @param permissions
         * 拒绝授权的列表
         */
        fun onRationale(permissions: Array<String>)

        /**
         * 拒绝授权回调
         *
         * @param permissions
         * 拒绝授权的列表
         */
        fun onDenied(permissions: Array<String>)
    }
}