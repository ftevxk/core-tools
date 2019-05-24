@file:Suppress("UNCHECKED_CAST", "unused", "LeakingThis")

package cn.ftevxk.base.extension

import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ftevxk.base.common.DataBindAdapter
import cn.ftevxk.base.common.IDataBindItemModel

/****************************************
 * RecyclerView相关扩展
 * 部分扩展涉及DataBindAdapter
 ***************************************/

/**
 * 获得item的ViewHolder
 */
fun RecyclerView.getItemViewHolder(position: Int): DataBindAdapter.ViewHolder? {
    return findViewHolderForAdapterPosition(position) as? DataBindAdapter.ViewHolder
}

/**
 * RecyclerView获得item的ViewDataBinding
 */
fun <VDB : ViewDataBinding> RecyclerView.getItemBinding(position: Int): VDB? {
    return getItemViewHolder(position)?.binding as? VDB
}

/**
 * ViewHolder获得item的ViewDataBinding
 */
fun <VDB : ViewDataBinding> RecyclerView.ViewHolder.getItemBinding(): VDB? {
    return (this as? DataBindAdapter.ViewHolder)?.binding as? VDB
}

/******************************************************************
 * 如果adapter为DataBindAdapter则提供相应数据操作方法
 *****************************************************************/

/**
 * 构建简单的列表类型RecyclerView
 * 分割线仅支持LinearLayoutManager
 */
fun RecyclerView.buildSimpleBindAdapter(divider: Boolean = true) {
    if (adapter == null) {
        adapter = DataBindAdapter()
    }
    if (layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
    if (divider) {
        if (layoutManager is LinearLayoutManager && layoutManager !is GridLayoutManager) {
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }
    }
}

fun RecyclerView.getDataBindAdapter(): DataBindAdapter? {
    return adapter as? DataBindAdapter
}

fun <T : IDataBindItemModel> RecyclerView.setItemModels(newModels: MutableList<T>) {
    getDataBindAdapter()?.setItemModels(newModels)
}

fun <T : IDataBindItemModel> RecyclerView.getItemModels(): MutableList<T>? {
    return getDataBindAdapter()?.getItemModels()
}

fun <T : IDataBindItemModel> RecyclerView.getItemModel(position: Int): T? {
    return getDataBindAdapter()?.getItemModel(position)
}

fun <T : IDataBindItemModel> RecyclerView.setItemModel(model: T, position: Int = -1, additional: Boolean = false) {
    getDataBindAdapter()?.setItemModel(model, position, additional)
}

fun RecyclerView.setBindAdapterListener(listener: DataBindAdapter.BindAdapterListener) {
    getDataBindAdapter()?.bindAdapterListener = listener
}

fun <T : IDataBindItemModel> RecyclerView.removeItemModel(model: T): Int?{
    return getDataBindAdapter()?.removeItemModel(model)
}

fun <T : IDataBindItemModel> RecyclerView.removeItemModel(index: Int): T?{
    return getDataBindAdapter()?.removeItemModel(index)
}

/****************************************
 * (START) 点击事件相关
 ***************************************/

/**
 * 扩展RecyclerView的点击与长按事件
 */
fun RecyclerView.setOnItemClickListener(
        onItemClickListener: ((holder: RecyclerView.ViewHolder, position: Int, location: Array<Float>) -> Unit)? = null,
        onItemLongClickListener: ((holder: RecyclerView.ViewHolder, position: Int, location: Array<Float>) -> Unit)? = null
) {
    val itemGesture = ItemGesture(this)
    itemGesture.onItemClickListener = onItemClickListener
    itemGesture.onItemLongClickListener = onItemLongClickListener
}

/**
 * item点击的是否为指定控件
 */
fun RecyclerView.isClickControlView(view: View?, location: Array<Float>): Boolean {
    if (view == null) return false
    val recyclerRect = Rect()
    this.getGlobalVisibleRect(recyclerRect)
    val viewRect = Rect()
    view.getGlobalVisibleRect(viewRect)
    if (location[0] >= viewRect.left - recyclerRect.left && location[0] <= viewRect.right - recyclerRect.left
            && location[1] >= viewRect.top - recyclerRect.top && location[1] <= viewRect.bottom - recyclerRect.top) {
        return true
    }
    return false
}

/****************************************
 * (END) 点击事件相关
 ***************************************/

/**
 * 手势事件封装类
 */
open class ItemGesture(private val recyclerView: RecyclerView) :
        GestureDetector.SimpleOnGestureListener(), RecyclerView.OnItemTouchListener {
    init {
        recyclerView.addOnItemTouchListener(this)
    }

    var onItemClickListener:
            ((holder: RecyclerView.ViewHolder, position: Int, location: Array<Float>) -> Unit)? = null

    var onItemLongClickListener:
            ((holder: RecyclerView.ViewHolder, position: Int, location: Array<Float>) -> Unit)? = null

    private var detector = GestureDetectorCompat(recyclerView.context, this)

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        detector.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    /**
     * 点击
     */
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            val position = recyclerView.getChildLayoutPosition(view)
            val holder =
                    recyclerView.findViewHolderForLayoutPosition(position) as RecyclerView.ViewHolder
            onItemClickListener?.invoke(holder, position, arrayOf(e.x, e.y))
            return true
        }
        return false
    }

    /**
     * 长按
     */
    override fun onLongPress(e: MotionEvent) {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            val position = recyclerView.getChildLayoutPosition(view)
            val holder =
                    recyclerView.findViewHolderForLayoutPosition(position) as RecyclerView.ViewHolder
            onItemLongClickListener?.invoke(holder, position, arrayOf(e.x, e.y))
        }
    }
}