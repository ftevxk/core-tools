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

/*****************************************************************
 * 点击事件相关
 * 可用于任意RecyclerView.Adapter，与DataBindAdapter无关
 *****************************************************************/

/**
 * 扩展RecyclerView的点击事件
 */
fun RecyclerView.setOnItemClickListener(onItemClickListener: ((
        holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null) {
    var itemGesture = getTag("ItemGesture".hashCode()) as? ItemGesture
    if (itemGesture == null) {
        itemGesture = ItemGesture(this)
        setTag("ItemGesture".hashCode(), itemGesture)
    }
    itemGesture.onItemClickListener = onItemClickListener
}

/**
 * 扩展RecyclerView的长按事件
 */
fun RecyclerView.setOnItemLongClickListener(onItemLongClickListener: ((
        holder: RecyclerView.ViewHolder, position: Int) -> Unit)? = null) {
    var itemGesture = getTag("ItemGesture".hashCode()) as? ItemGesture
    if (itemGesture == null) {
        itemGesture = ItemGesture(this)
        setTag("ItemGesture".hashCode(), itemGesture)
    }
    itemGesture.onItemLongClickListener = onItemLongClickListener
}

/**
 * item点击的位置是否为指定控件
 */
fun RecyclerView.ViewHolder.isClickControlView(view: View?): Boolean {
    if (view == null) return false
    val recyclerView = clickRecyclerView
    val clickLocation = clickLocation
    if (recyclerView == null || clickLocation == null) return false
    val recyclerRect = Rect()
    recyclerView.getGlobalVisibleRect(recyclerRect)
    val viewRect = Rect()
    view.getGlobalVisibleRect(viewRect)
    if (clickLocation[0] >= viewRect.left - recyclerRect.left &&
            clickLocation[0] <= viewRect.right - recyclerRect.left &&
            clickLocation[1] >= viewRect.top - recyclerRect.top &&
            clickLocation[1] <= viewRect.bottom - recyclerRect.top) {
        return true
    }
    return false
}

/**
 * Item点击时存取坐标位置
 */
var RecyclerView.ViewHolder.clickLocation: Array<Float>?
    get() = itemView.getTag("clickLocation".hashCode()) as? Array<Float>
    set(value) = itemView.setTag("clickLocation".hashCode(), value)

/**
 * Item点击时临时存取RecyclerView对象
 */
private var RecyclerView.ViewHolder.clickRecyclerView: RecyclerView?
    get() = itemView.getTag("clickRecyclerView".hashCode()) as? RecyclerView
    set(value) = itemView.setTag("clickRecyclerView".hashCode(), value)

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
            holder.clickRecyclerView = recyclerView
            holder.clickLocation = arrayOf(e.x, e.y)
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
            holder.clickRecyclerView = recyclerView
            holder.clickLocation = arrayOf(e.x, e.y)
            onItemLongClickListener?.invoke(holder, position)
        }
    }
}