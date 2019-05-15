package cn.ftevxk.base.common

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * DataBindAdapter监听
 */
interface SimpleBindAdapterListener {
    //onBindViewHolder数据绑定之前执行
    fun onBindViewHolderBefore(holder: DataBindAdapter.ViewHolder, position: Int){}
    //onBindViewHolder数据绑定之后执行
    fun onBindViewHolderAfter(holder: DataBindAdapter.ViewHolder, position: Int, model: IDataBindItemModel){}
    //需要处理局部刷新调用，返回true会中止后续onBindViewHolder(holder: ViewHolder, position: Int)的执行
    fun onBindViewHolder(holder: DataBindAdapter.ViewHolder, position: Int, payloads: MutableList<Any>) : Boolean? = null

    fun onCreateViewHolder(viewDataBinding: ViewDataBinding){}
    fun onViewAttachedToWindow(holder: DataBindAdapter.ViewHolder) {}
    fun onViewDetachedFromWindow(holder: DataBindAdapter.ViewHolder) {}
    fun onViewRecycled(holder: DataBindAdapter.ViewHolder) {}
    fun onFailedToRecycleView(holder: DataBindAdapter.ViewHolder): Boolean? = null
    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {}
    fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {}
}