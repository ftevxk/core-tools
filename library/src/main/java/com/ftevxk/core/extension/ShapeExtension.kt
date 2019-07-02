@file:Suppress("unused", "UNUSED_PARAMETER", "DefaultLocale")

package com.ftevxk.core.extension

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Shape类型枚举
 */
enum class ShapeType(value: String) {
    RECT("rect"), OVAL("oval"), LINE("line"), RING("ring")
}

object ShapeExtension {

    /**
     * 获得自定义Shape的Drawable
     * @param radius 接收参数Number或List<Number>(size = 4){左上，右上，左下，右下}
     */
    @JvmStatic
    fun getShapeDrawable(
            type: ShapeType,
            solidColor: Int? = null,
            strokeWidth: Number? = null,
            strokeColor: Int? = null,
            dashWidth: Number? = null,
            dashGap: Number? = null,
            radius: Any? = null): Drawable {
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
        when (radius) {
            is Number -> drawable.cornerRadius = radius.toFloat()
            is List<*> -> {
                drawable.cornerRadii = floatArrayOf(
                        (radius[0] as? Number)?.toFloat() ?: 0f,
                        (radius[0] as? Number)?.toFloat() ?: 0f,
                        (radius[1] as? Number)?.toFloat() ?: 0f,
                        (radius[1] as? Number)?.toFloat() ?: 0f,
                        (radius[2] as? Number)?.toFloat() ?: 0f,
                        (radius[2] as? Number)?.toFloat() ?: 0f,
                        (radius[3] as? Number)?.toFloat() ?: 0f,
                        (radius[3] as? Number)?.toFloat() ?: 0f
                )
            }
            null -> {
            }
            else -> throw Throwable("radius仅接收参数Number或List<Number>(size = 4){左上，右上，左下，右下}")
        }
        return drawable
    }
}

/**
 * Shape背景扩展设置
 * tools:shape_type="@{`rect`}" Shape类型设置 ==> 矩形: rect; 椭圆形: oval; 线形: line; 环形: ring
 * tools:unit="@{`dp`}" 单位类型设置 ==> px; dp; sp; pt; in; mm; 默认不设置为dp单位
 * tools:shape_radius="@{30}" 设置四周相同角度
 * tools:shape_radius="@{Arrays.asList(30, 30, 30, 30)}" 设置左上、右上、左下、右下的角度，需要引包 <import type="java.util.Arrays" />
 * 其余参照Shape用法，可设置pressed、checked、focused、selected、clickable、enabled等状态
 */
@BindingAdapter(value = [
    "tools:unit",
    //默认状态
    "tools:default_background",
    "tools:shape_type",
    "tools:shape_solid_color",
    "tools:shape_stroke_width",
    "tools:shape_stroke_color",
    "tools:shape_dash_width",
    "tools:shape_dash_gap",
    "tools:shape_radius",
    //pressed状态
    "tools:pressed_background",
    "tools:pressed_shape_type",
    "tools:pressed_shape_solid_color",
    "tools:pressed_shape_stroke_width",
    "tools:pressed_shape_stroke_color",
    "tools:pressed_shape_dash_width",
    "tools:pressed_shape_dash_gap",
    "tools:pressed_shape_radius",
    //checked状态
    "tools:checked_background",
    "tools:checked_shape_type",
    "tools:checked_shape_solid_color",
    "tools:checked_shape_stroke_width",
    "tools:checked_shape_stroke_color",
    "tools:checked_shape_dash_width",
    "tools:checked_shape_dash_gap",
    "tools:checked_shape_radius",
    //focused状态
    "tools:focused_background",
    "tools:focused_shape_type",
    "tools:focused_shape_solid_color",
    "tools:focused_shape_stroke_width",
    "tools:focused_shape_stroke_color",
    "tools:focused_shape_dash_width",
    "tools:focused_shape_dash_gap",
    "tools:focused_shape_radius",
    //selected状态
    "tools:selected_background",
    "tools:selected_shape_type",
    "tools:selected_shape_solid_color",
    "tools:selected_shape_stroke_width",
    "tools:selected_shape_stroke_color",
    "tools:selected_shape_dash_width",
    "tools:selected_shape_dash_gap",
    "tools:selected_shape_radius",
    //clickable状态
    "tools:clickable_background",
    "tools:clickable_shape_type",
    "tools:clickable_shape_solid_color",
    "tools:clickable_shape_stroke_width",
    "tools:clickable_shape_stroke_color",
    "tools:clickable_shape_dash_width",
    "tools:clickable_shape_dash_gap",
    "tools:clickable_shape_radius",
    //enabled状态
    "tools:enabled_background",
    "tools:enabled_shape_type",
    "tools:enabled_shape_solid_color",
    "tools:enabled_shape_stroke_width",
    "tools:enabled_shape_stroke_color",
    "tools:enabled_shape_dash_width",
    "tools:enabled_shape_dash_gap",
    "tools:enabled_shape_radius"
], requireAll = false)
fun View.setShapeBackground(
        unit: String? = null,
        //默认状态
        backgroundDrawable: Drawable? = null,
        typeStr: String? = null,
        solidColor: Int? = null,
        strokeWidth: Number? = null,
        strokeColor: Int? = null,
        dashWidth: Number? = null,
        dashGap: Number? = null,
        radius: Any? = null,
        //pressed状态
        backgroundPressed: Drawable? = null,
        typeStrPressed: String? = null,
        solidColorPressed: Int? = null,
        strokeWidthPressed: Number? = null,
        strokeColorPressed: Int? = null,
        dashWidthPressed: Number? = null,
        dashGapPressed: Number? = null,
        radiusPressed: Any? = null,
        //checked状态
        backgroundChecked: Drawable? = null,
        typeStrChecked: String? = null,
        solidColorChecked: Int? = null,
        strokeWidthChecked: Number? = null,
        strokeColorChecked: Int? = null,
        dashWidthChecked: Number? = null,
        dashGapChecked: Number? = null,
        radiusChecked: Any? = null,
        //focused状态
        backgroundFocused: Drawable? = null,
        typeStrFocused: String? = null,
        solidColorFocused: Int? = null,
        strokeWidthFocused: Number? = null,
        strokeColorFocused: Int? = null,
        dashWidthFocused: Number? = null,
        dashGapFocused: Number? = null,
        radiusFocused: Any? = null,
        //selected状态
        backgroundSelected: Drawable? = null,
        typeStrSelected: String? = null,
        solidColorSelected: Int? = null,
        strokeWidthSelected: Number? = null,
        strokeColorSelected: Int? = null,
        dashWidthSelected: Number? = null,
        dashGapSelected: Number? = null,
        radiusSelected: Any? = null,
        //clickable状态
        backgroundClickable: Drawable? = null,
        typeStrClickable: String? = null,
        solidColorClickable: Int? = null,
        strokeWidthClickable: Number? = null,
        strokeColorClickable: Int? = null,
        dashWidthClickable: Number? = null,
        dashGapClickable: Number? = null,
        radiusClickable: Any? = null,
        //enabled状态
        backgroundEnabled: Drawable? = null,
        typeStrEnabled: String? = null,
        solidColorEnabled: Int? = null,
        strokeWidthEnabled: Number? = null,
        strokeColorEnabled: Int? = null,
        dashWidthEnabled: Number? = null,
        dashGapEnabled: Number? = null,
        radiusEnabled: Any? = null
) {
    val stateListDrawable = StateListDrawable()

    //pressed状态
    addStateDrawable(android.R.attr.state_pressed, "pressed_",
            stateListDrawable, unit, backgroundPressed,
            typeStrPressed ?: typeStr, solidColorPressed,
            strokeWidthPressed, strokeColorPressed,
            dashWidthPressed, dashGapPressed, radiusPressed)

    //checked状态
    addStateDrawable(android.R.attr.state_checked, "checked_",
            stateListDrawable, unit, backgroundChecked,
            typeStrChecked ?: typeStr, solidColorChecked,
            strokeWidthChecked, strokeColorChecked,
            dashWidthChecked, dashGapChecked, radiusChecked)

    //focused状态
    addStateDrawable(android.R.attr.state_focused, "focused_",
            stateListDrawable, unit, backgroundFocused,
            typeStrFocused ?: typeStr, solidColorFocused,
            strokeWidthFocused, strokeColorFocused,
            dashWidthFocused, dashGapFocused, radiusFocused)

    //selected状态
    addStateDrawable(android.R.attr.state_selected, "selected_",
            stateListDrawable, unit, backgroundSelected,
            typeStrSelected ?: typeStr, solidColorSelected,
            strokeWidthSelected, strokeColorSelected,
            dashWidthSelected, dashGapSelected, radiusSelected)

    //checkable状态
    addStateDrawable(android.R.attr.state_checkable, "checkable_",
            stateListDrawable, unit, backgroundClickable,
            typeStrClickable ?: typeStr, solidColorClickable,
            strokeWidthClickable, strokeColorClickable,
            dashWidthClickable, dashGapClickable, radiusClickable)

    //enabled状态
    addStateDrawable(android.R.attr.state_enabled, "enabled_",
            stateListDrawable, unit, backgroundEnabled,
            typeStrEnabled ?: typeStr, solidColorEnabled,
            strokeWidthEnabled, strokeColorEnabled,
            dashWidthEnabled, dashGapEnabled, radiusEnabled)

    //默认状态
    addStateDrawable(0, "", stateListDrawable, unit,
            backgroundDrawable, typeStr, solidColor,
            strokeWidth, strokeColor, dashWidth, dashGap, radius)

    background = stateListDrawable
}

/**
 * 添加状态到StateListDrawable
 */
private fun View.addStateDrawable(
        state: Int,
        prefix: String,
        stateListDrawable: StateListDrawable,

        unit: String? = null,
        drawable: Drawable? = null,
        typeStr: String? = null,
        solidColor: Int? = null,
        strokeWidth: Number? = null,
        strokeColor: Int? = null,
        dashWidth: Number? = null,
        dashGap: Number? = null,
        radius: Any? = null) {

    if (drawable != null) {
        stateListDrawable.addState(intArrayOf(state), drawable)
    } else if (solidColor != null || (strokeWidth != null && strokeColor != null)) {
        stateListDrawable.addState(intArrayOf(state), getBindShapeDrawable(
                prefix, unit, typeStr, solidColor, strokeWidth,
                strokeColor, dashWidth, dashGap, radius))
    }
}

/**
 * BindingAdapter获取ShapeDrawable
 */
private fun View.getBindShapeDrawable(
        prefix: String,
        unit: String? = null,
        typeStr: String? = null,
        solidColor: Int? = null,
        strokeWidth: Number? = null,
        strokeColor: Int? = null,
        dashWidth: Number? = null,
        dashGap: Number? = null,
        radius: Any? = null): Drawable {

    val shapeType = when (typeStr?.trim()?.toLowerCase()) {
        "rect" -> ShapeType.RECT
        "oval" -> ShapeType.OVAL
        "line" -> ShapeType.LINE
        "ring" -> ShapeType.RING
        else -> throw Throwable("未设置Shape类型，需要添加:\n" +
                "\t\t\t\t矩形 => \n\t\t\t\ttools:${prefix}shape_type=\"@{`rect`}\"\n" +
                "\t\t\t\t椭圆形 => \n\t\t\t\ttools:${prefix}shape_type=\"@{`oval`}\"\n" +
                "\t\t\t\t线形 => \n\t\t\t\ttools:${prefix}shape_type=\"@{`line`}\"\n" +
                "\t\t\t\t环形 => \n\t\t\t\ttools:${prefix}shape_type=\"@{`ring`}\"\n"
        )
    }

    //将radius按单位值转换
    val unitRadius = when (radius) {
        is Number -> radius.getUnitValue(unit)
        is List<*> -> {
            listOf((radius[0] as? Number).getUnitValue(unit),
                (radius[1] as? Number).getUnitValue(unit),
                (radius[2] as? Number).getUnitValue(unit),
                (radius[3] as? Number).getUnitValue(unit))
        }
        else -> radius
    }

    return ShapeExtension.getShapeDrawable(shapeType, solidColor,
            strokeWidth.getUnitValue(unit), strokeColor,
            dashWidth.getUnitValue(unit), dashGap.getUnitValue(unit), unitRadius)
}