package com.android.jasper.jetpack.data

import android.graphics.Bitmap
import androidx.room.*
import java.util.*

/**
 *@author   Jasper
 *@create   2020/6/30 18:03
 *@describe
 *@update
 */
@Entity(
    tableName = "user_info",
    //phone 唯一
    indices = [Index(value = ["phone", "user_id"], unique = true)]
)
data class UserInfo @JvmOverloads constructor(

    /**
     * 手机号
     */
    @ColumnInfo(name = "phone")
    var phone: String = ""

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L

    @ColumnInfo(name = "user_id")
    var userId: String =""

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

