package com.android.jasper.framework.base

/**
 *@author   Jasper
 *@create   2020/6/15 11:16
 *@describe
 *@update
 */
open class SingletonHolder<out T : Any, in A>(private val newInstance:(A)  -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = newInstance(arg)
                instance = created
                created
            }
        }
    }

}
class test{
    fun test(){
        TestSingleton.getInstance("")
    }
}

class TestSingleton private constructor(s:String){
    companion object: SingletonHolder<TestSingleton, String>(::TestSingleton){

    }



}