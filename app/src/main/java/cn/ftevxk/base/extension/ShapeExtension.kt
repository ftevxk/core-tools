@file:Suppress("unused", "UNUSED_PARAMETER", "DefaultLocale")

package cn.ftevxk.base.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Shape类型枚举
 */
enum class ShapeType(value: String) {
    RECT("rect"), OVAL("oval"), LINE("line"), RING("ring")
}

/**
 * Shape背景扩展设置
 * bind:shape_type="@{`rect`}" Shape类型设置 ==> 矩形: rect; 椭圆形: oval; 线形: line; 环形: ring
 * bind:unit="@{`dp`}" 单位类型设置 ==> px; dp; sp; pt; in; mm; 默认dp
 * 其余参照Shape用法
 */
@BindingAdapter(value = ["bind:shape_type", "bind:unit", "bind:shape_solid_color",
    "bind:shape_stroke_width", "bind:shape_stroke_color", "bind:shape_dash_width", "bind:shape_dash_gap",
    "bind:shape_radius", "bind:shape_top_left_radius", "bind:shape_top_right_radius",
    "bind:shape_bottom_left_radius", "bind:shape_bottom_right_radius"], requireAll = false)
fun View.setShapeBackground(typeStr: String?, unit: String? = null,
                            solidColor: Int? = null, strokeWidth: Number? = null, strokeColor: Int? = null,
                            dashWidth: Number? = null, dashGap: Number? = null,
                            radius: Number? = null, topLeftRadius: Number? = null, topRightRadius: Number? = null,
                            bottomLeftRadius: Number? = null, bottomRightRadius: Number? = null) {
    val shapeType = when (typeStr?.trim()?.toLowerCase()) {
        "rect" -> ShapeType.RECT
        "oval" -> ShapeType.OVAL
        "line" -> ShapeType.LINE
        "ring" -> ShapeType.RING
        else -> throw Throwable("Shape类型:\n" +
                "\t\t\t\t矩形 => \n\t\t\t\tbind:shape_type=\"@{`rect`}\"\n" +
                "\t\t\t\t椭圆形 => \n\t\t\t\tbind:shape_type=\"@{`oval`}\"\n" +
                "\t\t\t\t线形 => \n\t\t\t\tbind:shape_type=\"@{`line`}\"\n" +
                "\t\t\t\t环形 => \n\t\t\t\tbind:shape_type=\"@{`ring`}\"\n"
        )
    }
    val drawable = context.getShapeDrawable(shapeType, solidColor,
            strokeWidth.getUnitValue(unit), strokeColor, dashWidth.getUnitValue(unit), dashGap.getUnitValue(unit),
            radius.getUnitValue(unit), topLeftRadius.getUnitValue(unit),
            topRightRadius.getUnitValue(unit), bottomLeftRadius.getUnitValue(unit), bottomRightRadius.getUnitValue(unit)
    )
    //设置背景
    this.background = drawable
//    //如果有虚线则关闭硬件加速，使用出现黑色背景
//    if (dashWidth != null && dashGap != null) {
//        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//    }
}

/**
 * 获得自定义Shape的Drawable
 */
fun Context.getShapeDrawable(type: ShapeType, solidColor: Int? = null,
                          strokeWidth: Number? = null, strokeColor: Int? = null,
                          dashWidth: Number? = null, dashGap: Number? = null,
                          radius: Number? = null, topLeftRadius: Number? = null, topRightRadius: Number? = null,
                          bottomLeftRadius: Number? = null, bottomRightRadius: Number? = null): Drawable {
    val drawable = GradientDrawable()
    drawable.shape = when (type) {
        ShapeType.RECT -> GradientDrawable.RECTANGLE
        ShapeType.OVAL -> GradientDrawable.OVAL
        ShapeType.LINE -> GradientDrawable.LINE
        ShapeType.RING -> GradientDrawable.RING
    }
    //设置填充色
    solidColor?.run { drawable.setColor(this) }
    //设置边框
    if (strokeWidth != null && strokeColor != null) {
        if (dashWidth != null && dashGap != null && dashWidth.toFloat() > 0f) {
            //边框是虚线的时候
            drawable.setStroke(strokeWidth.toInt(), strokeColor, dashWidth.toFloat(), dashGap.toFloat())
        } else {
            drawable.setStroke(strokeWidth.toInt(), strokeColor)
        }
    }
    //设置角度
    if (radius != null) {
        drawable.cornerRadius = radius.toFloat()
    } else {
        drawable.cornerRadii =
                floatArrayOf(
                        topLeftRadius?.toFloat() ?: 0f, topLeftRadius?.toFloat() ?: 0f,
                        topRightRadius?.toFloat() ?: 0f, topRightRadius?.toFloat() ?: 0f,
                        bottomRightRadius?.toFloat() ?: 0f, bottomRightRadius?.toFloat() ?: 0f,
                        bottomLeftRadius?.toFloat() ?: 0f, bottomLeftRadius?.toFloat() ?: 0f
                )
    }
    return drawable
}