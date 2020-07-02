package com.android.jasper.jetpack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *@author   Jasper
 *@create   2020/7/1 10:27
 *@describe
 *@update
 */
@Database(entities = [UserInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userInfoDao(): UserInfoDao


    companion object{

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val i = instance
            if (i != null) {
                return i
            }
            return synchronized(Any()) {
                val i2 = instance
                if (i2 != null) {
                    i2
                } else {
                    val database =Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "userInfo.db")
                        //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。
                        //他可能造成主线程lock以及anr
                        //所以我们的操作都是在新线程完成的
//                         .allowMainThreadQueries()
                        .build()
                    instance = database
                    database
                }
            }
        }

    }
}