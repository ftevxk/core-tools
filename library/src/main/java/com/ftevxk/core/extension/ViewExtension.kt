@file:Suppress("unused", "DEPRECATION")

package com.ftevxk.core.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import org.jetbrains.anko.firstChildOrNull
import kotlin.concurrent.thread

/**
 * 在XML布局里直接自定义drawable的宽高
 */
@BindingAdapter(value = ["drawable_size", "unit"], requireAll = false)
fun TextView.customDrawableSize(size: Number, unit: String? = null) {
    customDrawableSize(size, size, unit)
}

/**
 * 在XML布局里直接自定义drawable的宽高
 */
@BindingAdapter(value = ["drawable_width", "drawable_height", "unit"], requireAll = false)
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
 * TextView简单设置Html格式内容
 */
@BindingAdapter("html")
fun TextView.setSimpleHtml(html: String?) {
    if (!html.isNullOrEmpty()) {
        Html.fromHtml(html, Html.ImageGetter {
            thread {
                val drawable = Glide.with(this).load(it).submit().get()
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                post { this.text = Html.fromHtml(html, Html.ImageGetter { drawable }, null) }
            }
            null
        }, null)
    }
}

/**
 * TextView添加删除线
 */
@BindingAdapter("deleteLine")
fun TextView.addDeleteLine(deleteLine: Boolean) {
    if (deleteLine) {
        this.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
    }
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
 * 简化监听文字改变事件
 */
fun EditText.addTextChangeListener(
        beforeTextChangedListener: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
        afterTextChangedListener: ((s: Editable?) -> Unit)? = null,
        onTextChangedListener: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChangedListener?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChangedListener?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChangedListener?.invoke(s)
        }
    })
}

/**
 * 控件手势监听
 */
@SuppressLint("ClickableViewAccessibility")
fun View.addGesture(listener: GestureDetector.SimpleOnGestureListener) {
    val detector = GestureDetectorCompat(this.context, listener)
    this.setOnTouchListener { _, event ->
        detector.onTouchEvent(event)
    }
}

/**
 * 为控件设置外边距
 */
fun View.setMargins(left: Number? = null, top: Number? = null,
                    right: Number? = null, bottom: Number? = null,
                    horizontal: Number? = null, vertical: Number? = null) {
    post {
        val params = this.layoutParams
        if (params !is ViewGroup.MarginLayoutParams) {
            layoutParams = ViewGroup.MarginLayoutParams(params.width, params.height)
        }
        (params as ViewGroup.MarginLayoutParams).setMargins(
                (left?.toInt() ?: horizontal?.toInt()) ?: params.leftMargin,
                (top?.toInt() ?: vertical?.toInt()) ?: params.topMargin,
                (right?.toInt() ?: horizontal?.toInt()) ?: params.topMargin,
                (bottom?.toInt() ?: vertical?.toInt()) ?: params.topMargin
        )
        this.requestLayout()
    }
}

/**
 * 将View转换成Bitmap
 * @param transparent 是否为透明背景
 */
fun View.translateBitmap(result: (Bitmap) -> Unit, transparent: Boolean = true) {
    post {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (!transparent) canvas.drawColor(Color.WHITE)
        this.draw(canvas)
        result.invoke(bitmap)
    }
}

/**
 * 添加addOnGlobalLayoutListener监听
 */
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

/**
 * 重置View的大小
 */
fun View.resize(width: Int? = null, height: Int? = null) {
    val lp = layoutParams
    lp?.let {
        lp.width = width ?: lp.width
        lp.height = height ?: lp.height
        layoutParams = lp
    }
}

/**
 * 查找第一个符合类型的子child
 */
inline fun <reified V : View> ViewGroup.findFirstChild(): V? {
    return this.firstChildOrNull { return@firstChildOrNull it is V } as? V
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