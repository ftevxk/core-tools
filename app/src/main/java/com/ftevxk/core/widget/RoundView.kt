package com.ftevxk.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

class RoundView(context: Context, attr: AttributeSet?) : RelativeLayout(context, attr) {
    private val mPath: Path = Path()
    private var mRectF = RectF()

    init { setLayerType(View.LAYER_TYPE_SOFTWARE, null) }

    constructor(context: Context) : this(context, null)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPath.reset()
        mRectF.set(0F, 0F, w.toFloat(), h.toFloat())
        mPath.addRoundRect(mRectF, w.toFloat() / 2.0F, h.toFloat() / 2.0F, Path.Direction.CW)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        tryDrawCircleWithClipPath(canvas)
    }

    /**
     * 通过ClipPath的方式绘制遮罩
     */
    private fun tryDrawCircleWithClipPath(canvas: Canvas?) {
        canvas?.save()
        canvas?.clipPath(mPath)
        super.dispatchDraw(canvas)
        canvas?.restore()
    }
}