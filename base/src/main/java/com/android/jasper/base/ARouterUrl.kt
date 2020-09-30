package com.android.jasper.base

/**
 *@author   Jasper
 *@create   2020/7/7 09:45
 *@describe
 *@update
 */
object ARouterPathGroup {

    const val APP_GROUP = "/app/"
}


object ARounterPath {
    /**
     * 用户列表
     */
    const val USER_LIST = "${ARouterPathGroup.APP_GROUP}userList"
    const val ADD_USER = "${ARouterPathGroup.APP_GROUP}addUser"


}