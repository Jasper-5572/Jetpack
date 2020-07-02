package com.android.jasper.jetpack.data

import android.graphics.Bitmap
import androidx.room.*

/**
 *@author   Jasper
 *@create   2020/6/30 18:03
 *@describe
 *@update
 */
@Entity(
    tableName = "user_info",
    //phone 唯一
    indices = [Index(value = ["phone"], unique = true)]
)
data class UserInfo @JvmOverloads constructor(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    var userId: String = ""
) {

    /**
     * 手机号
     */
    @ColumnInfo(name = "phone")
    var phone: String = ""

    /**
     * 昵称
     */
    @ColumnInfo(name = "nick_name")
    var nickName: String = ""

    /**
     *
     */
    @ColumnInfo(name = "head_image")
    var headImage: String = ""

//    companion object{
//        UUID.randomUUID().toString()
//    }
}

