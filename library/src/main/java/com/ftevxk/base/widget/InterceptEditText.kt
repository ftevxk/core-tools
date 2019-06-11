@file:Suppress("unused")

package com.ftevxk.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText

/**
 * 带拦截的EditText
 * 可拦截字符输入、字符删除、按键输入、光标选择
 */
class InterceptEditText : EditText {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, android.R.attr.editTextStyle)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    private var interceptListener: InterceptListener? = null

    fun setInterceptListener(listener: InterceptListener) {
        interceptListener = listener
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        return InterceptInputWrapper(super.onCreateInputConnection(outAttrs))
    }

    private inner class InterceptInputWrapper(val target: InputConnection) :
        InputConnectionWrapper(target, false) {

        override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
            val result = interceptListener?.commitText(target, text, newCursorPosition)
            return result ?: super.commitText(text, newCursorPosition)
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            val result = interceptListener?.deleteSurroundingText(target, beforeLength, afterLength)
            //解决删除键未触发监听
            if (beforeLength == 1 && afterLength == 0) {
                return result ?: sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                        sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
            return result ?: super.deleteSurroundingText(beforeLength, afterLength)
        }

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            val result = interceptListener?.sendKeyEvent(target, event)
            return result ?: super.sendKeyEvent(event)
        }

        override fun setSelection(start: Int, end: Int): Boolean {
            val result = interceptListener?.setSelection(target, start, end)
            return result ?: super.setSelection(start, end)
        }
    }

    /**
     * 返回false可拦截事件
     * target调用对应方法修改数据
     */
    interface InterceptListener {
        fun commitText(target: InputConnection, text: CharSequence, newCursorPosition: Int): Boolean? = null
        fun deleteSurroundingText(target: InputConnection, beforeLength: Int, afterLength: Int): Boolean? = null
        fun sendKeyEvent(target: InputConnection, event: KeyEvent): Boolean? = null
        fun setSelection(target: InputConnection, start: Int, end: Int): Boolean? = null
    }
}