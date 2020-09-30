package com.android.jasper.framework.widget

import android.text.InputFilter
import android.text.Spanned

/**
 *@author   Jasper
 *@create   2020/7/31 10:38
 *@describe
 *@update
 */
/**
 *
 * @property decimalLength Int 小数位长度
 * @property integerLength Int 整数位长度
 * @constructor
 */
class NumberInputFilter constructor(private val decimalLength: Int, private val integerLength: Int = Int.MAX_VALUE) : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        //小数点的位置
        var pointPosition = -1
        val length = dest?.length ?: 0
        val point = "."
        source?.let { sourceChar ->
            //禁止小数位(如果小数长度小于1禁止输入.)
            if (point.contentEquals(sourceChar)) {
                if (decimalLength < 1) {
                    return ""
                } else if (length < 1) {
                    return "0$point"
                }
            }

            dest?.let { destSpanned ->
                //获取小数点位置
                for (i in 0 until length) {
                    val c = destSpanned[i]
                    if (c == '.') {
                        pointPosition = i
                        break
                    }
                }
                //光标在首位且已经输入过当前不能再输入0
                if (dend == 0 && length > 0 && "0".contentEquals(sourceChar)) {
                    return ""
                }

                //限制输入0之后只能输入小数点
                if (length == 1) {
                    val c = destSpanned[0]
                    if (c == '0' && !point.contentEquals(sourceChar) && dend != 0) {
                        return ""
                    }
                }
            }
            //已经输入过小数点
            if (pointPosition >= 0) {
                //已经输入过小数点禁止再输入小数点
                if (point.contentEquals(sourceChar)) {
                    return ""
                }
                val inputDecimalLength = length - pointPosition
                //光标在小数点前面,当前输入整数的长度大于限制的整数长度
                if (dend <= pointPosition && pointPosition > integerLength) {
                    return ""
                }
                //光标在小数点后面,当前输入小数的长度大于限制的小数长度
                if (dend > pointPosition && inputDecimalLength > decimalLength) {
                    return ""
                }
            }
            //没有输入过小数点
            else {
                //当前输入的不是小数点 且当前长度大于整数长度
                if (!point.contentEquals(sourceChar) && length >= integerLength) {
                    return ""
                }
                //当前输入的是小数点
                if (point.contentEquals(sourceChar)) {
                    //光标后面的长度
                    val cursorRearLength = length - dend
                    if (cursorRearLength > decimalLength) {
                        return ""
                    }
                    //光标在首位输入点前面补充0
                    if (dend == 0) {
                        return "0$point"
                    }
                }
            }

        }

        return source ?: ""
    }
}