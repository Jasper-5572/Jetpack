package com.android.jasper.jetpack.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

/**
 *@author   Jasper
 *@create   2020/7/1 10:27
 *@describe
 *@update
 */
@Database(entities = [UserInfo::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userInfoDao(): UserInfoDao

    companion object {
//        private val migration_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                //  创建新的临时表
//                database.execSQL("CREATE TABLE 'user_info_new' ('id' INTEGER NOT NULL ,'user_id' TEXT DEFAULT '' UNIQUE, 'nick_name' TEXT  NOT NULL, 'head_image' TEXT  NOT NULL,'phone' TEXT NOT NULL UNIQUE,PRIMARY KEY('id'))")
//                // 复制数据
//                database.execSQL("INSERT INTO 'user_info_new' ( 'nick_name', 'head_image','phone') SELECT  'nick_name', 'head_image','phone' FROM user_info")
//                // 删除表结构
//                database.execSQL("DROP TABLE 'user_info'")
//                // 临时表名称更改
//                database.execSQL("ALTER TABLE 'user_info_new' RENAME TO 'user_info'")
//            }
//
//        }

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
                    val database = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "userInfo.db"
                    )
                        //下面注释表示允许主线程进行数据库操作，但是不推荐这样做。 他可能造成主线程lock以及anr 所以我们的操作都是在新线程完成的
//                        .allowMainThreadQueries()
                        //添加迁移
//                        .addMigrations(migration_1_2)
                        .fallbackToDestructiveMigration()
                        .build()
                    instance = database
                    database
                }
            }
        }

    }


}