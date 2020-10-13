@file:Suppress("UNCHECKED_CAST", "unused", "LeakingThis")

package com.ftevxk.core.extension

import android.graphics.Rect
import android.os.Build
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ftevxk.core.adapter.DataBindAdapter
import com.ftevxk.core.adapter.IDataBindItemModel

/*****************************************************************
 * 点击事件相关
 * 可用于任意RecyclerView.Adapter，与DataBindAdapter无关
 *****************************************************************/

/**
 * 扩展RecyclerView的点击事件
 */
fun RecyclerView.setOnItemClickListener(
        onItemClickListener: ((holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null) {
    if (itemGesture == null) itemGesture = ItemGesture(this)
    itemGesture!!.onItemClickListener = onItemClickListener
}

/**
 * 扩展RecyclerView的长按事件
 */
fun RecyclerView.setOnItemLongClickListener(
        onItemLongClickListener: ((holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null) {
    if (itemGesture == null) itemGesture = ItemGesture(this)
    itemGesture!!.onItemLongClickListener = onItemLongClickListener
}

/**
 * item点击的位置是否为指定控件
 */
fun RecyclerView.ViewHolder.isClickControlView(view: View?): Boolean {
    if (view == null) return false
    val clickLocation = clickRawXY ?: return false
    val viewRect = Rect()
    view.getGlobalVisibleRect(viewRect)
    return viewRect.contains(clickLocation[0].toInt(), clickLocation[1].toInt())
}

/**
 * Item点击时存取坐标位置
 */
var RecyclerView.ViewHolder.clickRawXY: Array<Float>?
    get() = itemView.getTag("clickRawXY".hashCode()) as? Array<Float>
    set(value) = itemView.setTag("clickRawXY".hashCode(), value)

/**
 * 点击和长按事件共用ItemGesture对象
 */
private var RecyclerView.itemGesture: ItemGesture?
    get() = getTag("itemGesture".hashCode()) as? ItemGesture
    set(value) = setTag("itemGesture".hashCode(), value)

/**
 * 手势事件封装类
 */
open class ItemGesture(private val recyclerView: RecyclerView) :
        GestureDetector.SimpleOnGestureListener(), RecyclerView.OnItemTouchListener {

    init {
        recyclerView.addOnItemTouchListener(this)
    }

    var onItemClickListener: ((holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null

    var onItemLongClickListener: ((holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null

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
            holder.clickRawXY = arrayOf(e.rawX, e.rawY)
            view.performClick()
            onItemClickListener?.invoke(holder, position)
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
            holder.clickRawXY = arrayOf(e.rawX, e.rawY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.performLongClick(e.x, e.y)
            } else {
                view.performLongClick()
            }
            onItemLongClickListener?.invoke(holder, position)
        }
    }
}

/*****************************************************************
 * RecyclerView相关扩展
 * 部分扩展涉及DataBindAdapter
 *****************************************************************/

/**
 * 获得item的ViewHolder
 */
fun RecyclerView.getItemViewHolder(position: Int): RecyclerView.ViewHolder? {
    return findViewHolderForAdapterPosition(position)
}

/**
 * RecyclerView获得item的ViewDataBinding
 */
fun <VDB : ViewDataBinding> RecyclerView.getItemBinding(position: Int): VDB? {
    return (getItemViewHolder(position) as? DataBindAdapter.ViewHolder)?.binding as? VDB
}

/**
 * ViewHolder获得item的ViewDataBinding
 */
fun <VDB : ViewDataBinding> RecyclerView.ViewHolder.getItemBinding(): VDB? {
    return (this as? DataBindAdapter.ViewHolder)?.binding as? VDB
}

/*****************************************************************
 * 如果adapter为DataBindAdapter则提供相应数据操作方法
 *****************************************************************/

/**
 * 构建简单的列表类型RecyclerView
 * 分割线仅支持LinearLayoutManager、GridLayoutManager
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
        } else if (layoutManager is GridLayoutManager) {
            addItemDecoration(DividerItemDecoration(context, GridLayoutManager.HORIZONTAL))
            addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        }
    }
}

/**
 * XML布局绑定DataBindAdapter
 */
@BindingAdapter("bind_data_adapter")
fun RecyclerView.bindDataAdapter(bindDataAdapter: Boolean) {
    if (bindDataAdapter) {
        this.adapter = DataBindAdapter()
    }
}

/**
 * 设置DataBindAdapter绑定空数据页面，自动根据Item数量处理空数据页面显示隐藏
 */
@BindingAdapter("empty_view")
fun RecyclerView.bindEmptyView(emptyView: View) {
    getDataBindAdapter()?.bindAdapterListener = object : DataBindAdapter.BindAdapterListener {
        override fun onNotifyChange(oldModels: MutableList<IDataBindItemModel>,
                                    newModels: MutableList<IDataBindItemModel>) {
            super.onNotifyChange(oldModels, newModels)
            emptyView.visibility = if (newModels.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

fun RecyclerView.getDataBindAdapter(): DataBindAdapter? {
    return adapter as? DataBindAdapter
}

fun <T : IDataBindItemModel> RecyclerView.diffItemModels(newModels: MutableList<T>) {
    getDataBindAdapter()?.diffItemModels(newModels)
}

fun <T : IDataBindItemModel> RecyclerView.setItemModels(newModels: MutableList<T>) {
    getDataBindAdapter()?.setItemModels(newModels)
}

fun <T : IDataBindItemModel> RecyclerView.addItemModels(newModels: MutableList<T>) {
    getDataBindAdapter()?.addItemModels(newModels)
}

fun <T : IDataBindItemModel> RecyclerView.getItemModels(): MutableList<T>? {
    return getDataBindAdapter()?.getItemModels()
}

fun <T : IDataBindItemModel> RecyclerView.getItemModel(position: Int): T? {
    return getDataBindAdapter()?.getItemModel(position)
}

fun <T : IDataBindItemModel> RecyclerView.setItemModel(
        model: T, position: Int = -1, additional: Boolean = false): DataBindAdapter? {
    return getDataBindAdapter()?.setItemModel(model, position, additional)
}

fun RecyclerView.setBindAdapterListener(listener: DataBindAdapter.BindAdapterListener) {
    getDataBindAdapter()?.bindAdapterListener = listener
}

fun RecyclerView.removeItemModel(model: IDataBindItemModel): Int? {
    return getDataBindAdapter()?.removeItemModel(model)
}

fun RecyclerView.removeItemModel(index: Int): IDataBindItemModel? {
    return getDataBindAdapter()?.removeItemModel(index)
}

fun <T : IDataBindItemModel> RecyclerView.ViewHolder.getItemModels(): MutableList<T>? {
    return (this as? DataBindAdapter.ViewHolder)?.adapter?.getItemModels()
}

fun <T : IDataBindItemModel> RecyclerView.ViewHolder.getItemModel(position: Int): T? {
    return (this as? DataBindAdapter.ViewHolder)?.adapter?.getItemModel(position)
}