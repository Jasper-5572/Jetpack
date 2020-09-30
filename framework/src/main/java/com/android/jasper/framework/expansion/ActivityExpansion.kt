package com.android.jasper.framework.expansion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.fragment.app.FragmentActivity
import com.android.jasper.framework.PermissionResult
import com.android.jasper.framework.ProxyFragmentManager
import com.android.jasper.framework.util.DeviceUtils

/**
 *@author   Jasper
 *@create   2020/7/9 14:53
 *@describe
 *@update
 */

/**
 * 设置状态栏颜色
 * @receiver FragmentActivity
 * @param color String
 */
fun FragmentActivity.setStatusColor(color: String) {
    safeRun {
        setActivityStatusColor(Color.parseColor(color))
    }
}

/**
 * 设置状态栏颜色
 * @receiver FragmentActivity
 * @param colorId Int
 */
@Suppress("DEPRECATION")
fun FragmentActivity.setStatusColor(@ColorRes colorId: Int) {
    setActivityStatusColor(resources.getColor(colorId))
}

/**
 * 跳转页面 并根据页面返回结果处理
 * @receiver FragmentActivity
 * @param intent Intent
 * @param resultBlock Function1<[@kotlin.ParameterName] Intent?, Unit>
 */
fun FragmentActivity.startActivityResult(intent: Intent, resultBlock: (resultIntent: Intent?) -> Unit) {
    ProxyFragmentManager.of(this).startActivityResult(intent) { resultCode: Int, resultIntent: Intent? ->
        if (resultCode == Activity.RESULT_OK) {
            resultBlock.invoke(resultIntent)
        }
    }
}

/**
 * 请求权限并回调
 * @receiver FragmentActivity
 * @param permissionSet Set<String>
 * @param permissionResult PermissionResult
 */
fun FragmentActivity.requestPermissionsResult(permissionSet: Set<String>, permissionResult: PermissionResult) {
    ProxyFragmentManager.of(this).requestPermissionsResult(permissionSet = permissionSet, permissionResult = permissionResult)
}

private fun FragmentActivity.setActivityStatusColor(@ColorInt colorId: Int) {
    when {
        //6.0以上版本
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            window?.let { activityWindow ->
                //设置状态栏底色颜色
                activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activityWindow.statusBarColor = colorId
//                setStatusBarDarkTheme(activity, true)
            }
        }
        //5.0以上版本
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            window?.let { activityWindow ->
                activityWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activityWindow.statusBarColor = colorId
//                setStatusBarDarkTheme(activity, true)
            }


        }
        //4.4以上版本 使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
//            setTranslucentStatus(activity);
//            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
//            //显示状态栏
//            tintManager.setStatusBarTintEnabled(true);
//            //设置状态栏颜色
//            tintManager.setStatusBarTintResource(
//                activity.getResources().getColor(R.color.color_00)
//            )

        }
    }
}

/**
 * 设置状态栏字体颜色是否为黑色
 * @receiver FragmentActivity
 * @param dark Boolean
 */
fun FragmentActivity.setStatusBarText(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        when {
            //6.0以上手机 系统支持修改状态栏字体颜色
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                setGoogleStatusBarText(dark)
            }
            //小米手机
            DeviceUtils.isMiui() -> {
                setMiuiStatusBarText(dark)
            }
            //魅族手机
            DeviceUtils.isFlyme() -> {
                setFlymeStatusBarText(dark)
            }
        }
    }
}

/**
 * 设置状态栏透明 4.4以上版本
 * @receiver FragmentActivity
 */
fun FragmentActivity.setStatusTranslucent() {
    //5.0以上版本 开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window?.let { activityWindow ->
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activityWindow.decorView.systemUiVisibility = option
            activityWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activityWindow.statusBarColor = Color.TRANSPARENT
            //导航栏颜色也可以正常设置
            //activityWindow.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
    //4.4以上版本
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window?.let { activityWindow ->
            activityWindow.attributes = activityWindow.attributes?.apply {
                flags = flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            }
        }

    }
}


/**
 * google修改字体颜色 设置Flyme 状态栏深色浅色切换
 * @receiver FragmentActivity
 * @param dark Boolean
 */
private fun FragmentActivity.setGoogleStatusBarText(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window?.decorView?.let { view ->
            val option = if (dark) {
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            if (view.systemUiVisibility != option) {
                view.systemUiVisibility = option
            }
        }
    }
}

/**
 * 魅族修改字体颜色 设置Flyme 状态栏深色浅色切换
 * @receiver FragmentActivity
 * @param dark Boolean
 */
private fun FragmentActivity.setFlymeStatusBarText(dark: Boolean) {
    try {
        window?.let {
            val lp = it.attributes
            val darkFlag =
                    WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            val value = if (dark) {
                meizuFlags.getInt(lp) or bit
            } else {
                meizuFlags.getInt(lp) and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            it.attributes = lp
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 小米修改状态栏字体 设置MIUI 状态栏深色浅色切换
 * @receiver FragmentActivity
 * @param dark Boolean
 */
private fun FragmentActivity.setMiuiStatusBarText(dark: Boolean) {
    try {
        window?.let {
            val clazz: Class<*> = it.javaClass
            @SuppressLint("PrivateApi") val layoutParams =
                    Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field =
                    layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getDeclaredMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
            )
            extraFlagField.isAccessible = true
            if (dark) {
                //状态栏亮色且黑色字体.
                extraFlagField.invoke(it, darkModeFlag, darkModeFlag)
            } else {
                extraFlagField.invoke(it, 0, darkModeFlag)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



