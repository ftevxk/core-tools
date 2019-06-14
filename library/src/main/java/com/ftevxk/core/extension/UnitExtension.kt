@file:Suppress("unused", "DefaultLocale", "UNCHECKED_CAST")

package com.ftevxk.core.extension

import android.content.res.Resources
import android.util.TypedValue

/****************************************
 * 单位值转换相关扩展
 ***************************************/

fun <T : Number> Any.getPxUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("px"), value)
}

fun <T : Number> Any.getDpUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("dp"), value)
}

fun <T : Number> Any.getSpUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("sp"), value)
}

fun <T : Number> Any.getPtUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("pt"), value)
}

fun <T : Number> Any.getInUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("in"), value)
}

fun <T : Number> Any.getMmUnit(value: T): T {
    return transformUnitValue(value.getUnitValue("mm"), value)
}

/**
 * 单位类型转换，默认单位DP
 */
fun Number?.getUnitValue(unitStr: String?): Float {
    if (this == null) return 0f
    val unit = when (unitStr?.trim()?.toLowerCase()) {
        "px" -> TypedValue.COMPLEX_UNIT_PX
        "dp" -> TypedValue.COMPLEX_UNIT_DIP
        "sp" -> TypedValue.COMPLEX_UNIT_SP
        "pt" -> TypedValue.COMPLEX_UNIT_PT
        "in" -> TypedValue.COMPLEX_UNIT_IN
        "mm" -> TypedValue.COMPLEX_UNIT_MM
        null -> TypedValue.COMPLEX_UNIT_DIP
        else -> throw Throwable("单位值仅可以设置: px、dp、sp、pt、in、mm")
    }
    return TypedValue.applyDimension(unit, this.toFloat(), Resources.getSystem().displayMetrics)
}

/**
 * 转化单位值泛型
 */
private fun <T : Number> transformUnitValue(value: Float, type: T): T {
    return when (type) {
        is Int -> value.toInt() as T
        is Double -> value.toDouble() as T
        is Long -> value.toLong() as T
        is Short -> value.toShort() as T
        else -> value as T
    }
}