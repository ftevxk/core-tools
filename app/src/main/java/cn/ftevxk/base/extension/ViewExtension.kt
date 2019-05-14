@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.content.Context
import android.view.GestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter

/**
 * 在XML布局里直接自定义drawable的宽高
 */
@BindingAdapter(value = ["bind:drawable_width", "bind:drawable_height", "bind:unit"], requireAll = false)
fun TextView.customDrawableSize(width: Number, height: Number, unit: String? = null) {
    val drawWidth = width.getUnitValue(unit).toInt()
    val drawHeight = height.getUnitValue(unit).toInt()
    if (compoundDrawables[0] != null) compoundDrawables[0].setBounds(0, 0, drawWidth, drawHeight)
    if (compoundDrawables[1] != null) compoundDrawables[1].setBounds(0, 0, drawWidth, drawHeight)
    if (compoundDrawables[2] != null) compoundDrawables[2].setBounds(0, 0, drawWidth, drawHeight)
    if (compoundDrawables[3] != null) compoundDrawables[3].setBounds(0, 0, drawWidth, drawHeight)
    setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
}

/**
 * EditText改变输入法状态
 * @param showInput 是否显示输入法
 */
fun EditText.inputState(showInput: Boolean) {
    this.isFocusable = true
    this.isFocusableInTouchMode = true
    val manager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (showInput) {
        this.requestFocus()
        manager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    } else {
        this.clearFocus()
        manager.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

/**
 * 控件手势监听
 */
fun View.addGesture(listener: GestureDetector.SimpleOnGestureListener) {
    val detector = GestureDetectorCompat(this.context, listener)
    this.setOnTouchListener { _, event ->
        detector.onTouchEvent(event)
    }
}

/**
 * 为控件设置外边距
 * @return 该控件是否可以设置外边距
 */
fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null): Boolean {
    val params = this.layoutParams
    if (params is ViewGroup.MarginLayoutParams) {
        val l = left ?: params.leftMargin
        val t = top ?: params.topMargin
        val r = right ?: params.rightMargin
        val b = bottom ?: params.bottomMargin
        params.setMargins(l, t, r, b)
        this.requestLayout()
        return true
    }
    return false
}