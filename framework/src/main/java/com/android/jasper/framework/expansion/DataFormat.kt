package com.android.jasper.framework.expansion

import android.content.res.Resources
import android.util.TypedValue
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 *@author   Jasper
 *@create   2020/6/4 13:43
 *@describe
 *@update
 */
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
val Float.px
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this,
        Resources.getSystem().displayMetrics
    )
val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * String->Int
 * @receiver String
 * @param defaultValue Int
 * @return Int
 */
fun String.parseInt(defaultValue: Int = 0): Int {
    return if (isEmpty()) {
        defaultValue
    } else {
        try {
            toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * String->Float
 * @receiver String
 * @param defaultValue Float
 * @return Float
 */
fun String.parseFloat(defaultValue: Float = 0f): Float {
    return if (isEmpty()) {
        defaultValue
    } else {
        try {
            toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * String->Double
 * @receiver String
 * @param defaultValue Double
 * @return Double
 */
fun String.parseDouble(defaultValue: Double = 0.0): Double {
    return if (isEmpty()) {
        defaultValue
    } else {
        try {
            toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            defaultValue
        }
    }
}

/**
 * String->Long
 * @receiver String
 * @param defaultValue Long
 * @return Long
 */
fun String.parseLong(defaultValue: Long = 0L): Long {
    return if (isEmpty()) {
        defaultValue
    } else {
        try {
            toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            defaultValue
        }
    }

}

/**
 * String->指定格式的String
 * @receiver String
 * @param pattern String
 * @param defaultValue Double
 * @return String
 */
fun String.formatString(pattern: String, defaultValue: Double = 0.0): String {
    return DecimalFormat(pattern).format(this.parseDouble(defaultValue))
}

/**
 * String->指定格式Double
 * @receiver String
 * @param pattern String
 * @param defaultValue Double
 * @return Double
 */
fun String.formatDouble(pattern: String, defaultValue: Double = 0.0): Double {
    return parseDouble(defaultValue).formatDouble(pattern)
}

/**
 * 根据指定pattern格式 Long字符串转换成时间戳
 * @receiver String
 * @param pattern String
 * @return String
 */
fun String.formatTimeString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(parseLong())
}

/**
 * 解析时间格式为对应的时间戳
 * @receiver String
 * @param pattern String
 * @param defaultValue Long
 * @return Long
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun String.formatTimeLong(pattern: String = "yyyy-MM-dd HH:mm:ss", defaultValue: Long = 0L): Long {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this).time
    } catch (e: ParseException) {
        e.printStackTrace()
        defaultValue
    }
}


/**
 * 将double类型的字符串每隔3位用逗号拼接起来
 * @receiver String
 * @param deleteZero Boolean
 * @return String
 */
fun String.formatStringWithCommaSplit(deleteZero: Boolean = false): String {
    val value: String = if (deleteZero) formatString("#0.########") else this
    if (isEmpty()) {
        return value
    }
    val sb = StringBuilder()
    val index = lastIndexOf(".")
    // 取出小数点前面的字符串
    var result: String
    // 存放小数点后面的内容
    val decimalString: String
    if (index == -1) {
        result = value
        decimalString = ""
    } else {
        result = value.substring(0, index)
        decimalString = value.substring(index)
    }
    // 将字符串颠倒过来
    result = StringBuilder(result).reverse().toString()
    // 将字符串每三位分隔开
    val size = if (result.length % 3 == 0) result.length / 3 else result.length / 3 + 1
    for (i in 0 until size - 1) {
        // 前n-1段都用逗号连接上
        sb.append(result.substring(i * 3, i * 3 + 3)).append(",")
    }
    //将前n-1段和第n段连接在一起
    if (size < 1) {
        return value
    }
    result = sb.toString() + result.substring((size - 1) * 3)
    result = StringBuilder(result).reverse().append(decimalString).toString()
    return result
}

/**
 * 根据指定格式pattern Double转换成Double
 * @receiver Double
 * @param pattern String
 * @return Double
 */
fun Double.formatDouble(pattern: String = "#0.00"): Double {
    return DecimalFormat(pattern).format(this).parseDouble()
}

/**
 * 根据指定格式pattern Double类型转换成String
 * @receiver Double
 * @param pattern String
 * @return String
 */
fun Double.formatString(pattern: String = "#0.00"): String {
    return DecimalFormat(pattern).format(this)
}

/**
 * Long类型数据转换成时间格式
 * @receiver Long
 * @param pattern String
 * @return String
 */
fun Long.formatTimeString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

/**
 * Double->Int
 * @receiver Double
 * @return Int
 */
fun Double.parseInt(): Int {
    return this.toInt()
}

/**
 * Double->Long
 * @receiver Double
 * @return Long
 */
fun Double.parseLong(): Long {
    return this.toLong()
}

/**
 * 将double类型的字符串每隔3位用逗号拼接起来
 */
fun Double.formatStringWithCommaSplit(
    newScale: Int = 8,
    roundingMode: Int = BigDecimal.ROUND_UP
): String {
    return BigDecimal(this.toString()).setScale(newScale, roundingMode).toPlainString()
        .formatStringWithCommaSplit(true)
}