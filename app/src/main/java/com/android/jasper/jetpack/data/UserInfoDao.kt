package com.android.jasper.jetpack.data

import androidx.room.*
import retrofit2.http.DELETE

/**
 *@author   Jasper
 *@create   2020/7/1 09:55
 *@describe
 *@update
 */
@Dao
interface UserInfoDao {

    @Query("SELECT * FROM USER_INFO")
    fun getUserInfoList(): MutableList<UserInfo>

    @Query("SELECT * FROM USER_INFO WHERE user_id=:userId")
    fun getUserInfoByUserId(userId: String): UserInfo?


    @Query("SELECT * FROM USER_INFO WHERE phone=:phone")
    fun getUserInfoByPhone(phone: String): UserInfo?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInfo: UserInfo)

    @Update
    fun update(userInfo: UserInfo)


    @Query("DELETE FROM USER_INFO WHERE user_id=:userId")
    fun delete(userId: String)

}