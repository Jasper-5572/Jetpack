package com.android.jasper.framework.expansion

import android.content.res.Resources
import android.util.TypedValue
import java.math.BigDecimal


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
 * 将double类型的字符串每隔3位用逗号拼接起来
 */
fun Double.formatStringWithCommaSplit(
    newScale: Int = 8,
    roundingMode: Int = BigDecimal.ROUND_UP
): String {
    return BigDecimal(this.toString()).setScale(newScale, roundingMode).toPlainString()
        .formatStringWithCommaSplit(true)
}