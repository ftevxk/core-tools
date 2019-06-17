@file:Suppress("unused", "UNUSED_PARAMETER", "DefaultLocale")

package com.ftevxk.core.extension

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import androidx.databinding.BindingAdapter
import java.util.*

/**
 * Shape类型枚举
 */
enum class ShapeType(value: String) {
    RECT("rect"), OVAL("oval"), LINE("line"), RING("ring")
}

object ShapeExtension {

    /**
     * 获得自定义Shape的Drawable
     * @param radius 接收参数Number或List<Number>(size = 4)，List参数对应的角度为 左上、右上、左下、右下
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
        }
        return drawable
    }
}

/**
 * Shape背景扩展设置
 * bind:shape_type="@{`rect`}" Shape类型设置 ==> 矩形: rect; 椭圆形: oval; 线形: line; 环形: ring
 * bind:unit="@{`dp`}" 单位类型设置 ==> px; dp; sp; pt; in; mm; 默认dp
 * bind:shape_radius="@{30}" 设置四周相同角度
 * bind:shape_radius="@{Arrays.asList(30, 30, 30, 30)}" 设置左上、右上、左下、右下的角度，需要引包 <import type="java.util.Arrays" />
 * 其余参照Shape用法，可设置pressed、checked、focused、selected、clickable、enabled等状态
 */
@BindingAdapter(value = [
    "bind:unit",
    //默认状态
    "bind:default_background",
    "bind:shape_type",
    "bind:shape_solid_color",
    "bind:shape_stroke_width",
    "bind:shape_stroke_color",
    "bind:shape_dash_width",
    "bind:shape_dash_gap",
    "bind:shape_radius",
    //pressed状态
    "bind:pressed_background",
    "bind:pressed_shape_type",
    "bind:pressed_shape_solid_color",
    "bind:pressed_shape_stroke_width",
    "bind:pressed_shape_stroke_color",
    "bind:pressed_shape_dash_width",
    "bind:pressed_shape_dash_gap",
    "bind:pressed_shape_radius",
    //checked状态
    "bind:checked_background",
    "bind:checked_shape_type",
    "bind:checked_shape_solid_color",
    "bind:checked_shape_stroke_width",
    "bind:checked_shape_stroke_color",
    "bind:checked_shape_dash_width",
    "bind:checked_shape_dash_gap",
    "bind:checked_shape_radius",
    //focused状态
    "bind:focused_background",
    "bind:focused_shape_type",
    "bind:focused_shape_solid_color",
    "bind:focused_shape_stroke_width",
    "bind:focused_shape_stroke_color",
    "bind:focused_shape_dash_width",
    "bind:focused_shape_dash_gap",
    "bind:focused_shape_radius",
    //selected状态
    "bind:selected_background",
    "bind:selected_shape_type",
    "bind:selected_shape_solid_color",
    "bind:selected_shape_stroke_width",
    "bind:selected_shape_stroke_color",
    "bind:selected_shape_dash_width",
    "bind:selected_shape_dash_gap",
    "bind:selected_shape_radius",
    //clickable状态
    "bind:clickable_background",
    "bind:clickable_shape_type",
    "bind:clickable_shape_solid_color",
    "bind:clickable_shape_stroke_width",
    "bind:clickable_shape_stroke_color",
    "bind:clickable_shape_dash_width",
    "bind:clickable_shape_dash_gap",
    "bind:clickable_shape_radius",
    //enabled状态
    "bind:enabled_background",
    "bind:enabled_shape_type",
    "bind:enabled_shape_solid_color",
    "bind:enabled_shape_stroke_width",
    "bind:enabled_shape_stroke_color",
    "bind:enabled_shape_dash_width",
    "bind:enabled_shape_dash_gap",
    "bind:enabled_shape_radius"
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
    addStateDrawable(android.R.attr.state_pressed, stateListDrawable, unit,
            backgroundPressed, typeStrPressed, solidColorPressed, strokeWidthPressed, strokeColorPressed,
            dashWidthPressed, dashGapPressed, radiusPressed)

    //checked状态
    addStateDrawable(android.R.attr.state_checked, stateListDrawable, unit,
            backgroundChecked, typeStrChecked, solidColorChecked, strokeWidthChecked, strokeColorChecked,
            dashWidthChecked, dashGapChecked, radiusChecked)

    //focused状态
    addStateDrawable(android.R.attr.state_focused, stateListDrawable, unit,
            backgroundFocused, typeStrFocused, solidColorFocused, strokeWidthFocused, strokeColorFocused,
            dashWidthFocused, dashGapFocused, radiusFocused)

    //selected状态
    addStateDrawable(android.R.attr.state_selected, stateListDrawable, unit,
            backgroundSelected, typeStrSelected, solidColorSelected, strokeWidthSelected, strokeColorSelected,
            dashWidthSelected, dashGapSelected, radiusSelected)

    //checkable状态
    addStateDrawable(android.R.attr.state_checkable, stateListDrawable, unit,
            backgroundClickable, typeStrClickable, solidColorClickable, strokeWidthClickable, strokeColorClickable,
            dashWidthClickable, dashGapClickable, radiusClickable)

    //enabled状态
    addStateDrawable(android.R.attr.state_enabled, stateListDrawable, unit,
            backgroundEnabled, typeStrEnabled, solidColorEnabled, strokeWidthEnabled, strokeColorEnabled,
            dashWidthEnabled, dashGapEnabled, radiusEnabled)

    //默认状态
    addStateDrawable(0, stateListDrawable, unit, backgroundDrawable, typeStr,
            solidColor, strokeWidth, strokeColor, dashWidth, dashGap, radius)

    background = stateListDrawable
}

/**
 * 添加状态到StateListDrawable
 */
private fun View.addStateDrawable(
        state: Int,
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
    } else if (typeStr != null && (solidColor != null || (strokeWidth != null && strokeColor != null))) {
        stateListDrawable.addState(intArrayOf(state), getBindShapeDrawable(unit, typeStr, solidColor, strokeWidth,
                strokeColor, dashWidth, dashGap, radius))
    }
}

/**
 * BindingAdapter获取ShapeDrawable
 */
private fun View.getBindShapeDrawable(
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
        else -> throw Throwable("Shape类型:\n" +
                "\t\t\t\t矩形 => \n\t\t\t\tbind:shape_type=\"@{`rect`}\"\n" +
                "\t\t\t\t椭圆形 => \n\t\t\t\tbind:shape_type=\"@{`oval`}\"\n" +
                "\t\t\t\t线形 => \n\t\t\t\tbind:shape_type=\"@{`line`}\"\n" +
                "\t\t\t\t环形 => \n\t\t\t\tbind:shape_type=\"@{`ring`}\"\n"
        )
    }

    //将radius按单位值转换
    val unitRadius = when (radius) {
        is Number -> radius.getUnitValue(unit)
        is List<*> -> {
            Arrays.asList((radius[0] as? Number).getUnitValue(unit),
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