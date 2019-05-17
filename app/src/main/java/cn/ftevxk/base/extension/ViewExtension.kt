@file:Suppress("unused")

package cn.ftevxk.base.extension

import android.content.Context
import android.content.ContextWrapper
import android.view.GestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

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
 */
fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    post {
        val params = this.layoutParams
        if (params !is ViewGroup.MarginLayoutParams) {
            layoutParams = ViewGroup.MarginLayoutParams(params.width, params.height)
        }
        (params as ViewGroup.MarginLayoutParams).setMargins(
            left ?: params.leftMargin,
            top ?: params.topMargin,
            right ?: params.rightMargin,
            bottom ?: params.bottomMargin
        )
        this.requestLayout()
    }
}

/**
 * 通过View找到所属FragmentActivity
 */
fun View.findActivity(): FragmentActivity? {
    return context.findActivity()
}

/**
 * 通过控件找到所属Fragment(AndroidX)
 */
fun View.findFragment(): Fragment? {
    return findViewHost() as? Fragment
}

/**
 * 通过控件查找宿主(FragmentActivity或Fragment)
 */
fun View.findViewHost(): Any? {
    val activity = findActivity() ?: return null
    val activityRoot = activity.findViewById<View>(android.R.id.content)
    val viewWithFragmentMaps = mutableMapOf<View, Fragment>()
    putViewWithFragmentMaps(activity.supportFragmentManager.fragments, viewWithFragmentMaps)
    var currentView: View = this
    while (currentView != activityRoot) {
        if (viewWithFragmentMaps.containsKey(currentView)) {
            return viewWithFragmentMaps[currentView]
        } else {
            val parent = currentView.parent
            if (parent is View) {
                currentView = parent
            }
        }
    }
    return activity
}

/**
 * 通过Context找到所属FragmentActivity
 */
fun Context.findActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) {
            return current
        }
        current = current.baseContext
    }
    return null
}

private fun putViewWithFragmentMaps(fragments: List<Fragment>?, maps: MutableMap<View, Fragment>) {
    fragments?.forEach {
        if (it.view != null) {
            maps[it.view!!] = it
            putViewWithFragmentMaps(it.childFragmentManager.fragments, maps)
        }
    }
}