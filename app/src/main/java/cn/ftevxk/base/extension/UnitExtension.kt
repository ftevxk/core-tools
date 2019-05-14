@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.content.res.Resources
import android.util.TypedValue

/****************************************
 * 单位值转换相关扩展
 * 例: 100.toDpUnit()
 ***************************************/

fun Number.toPxUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Number.toDpUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Number.toSpUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Number.toPtUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Number.toInUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Number.toMmUnit(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, this.toFloat(), Resources.getSystem().displayMetrics)
}


/**
 * 单位类型转换，当前主要供@BindingAdapter使用，默认单位DP
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