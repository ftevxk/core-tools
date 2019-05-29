@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package cn.ftevxk.base.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * DataBinding通用RecyclerView.Adapter封装
 * 需要配合DataBindItemModel作为数据源使用
 * Created by ftevxk on 19-04-26.
 */
class DataBindAdapter : RecyclerView.Adapter<DataBindAdapter.ViewHolder>() {

    private var models: MutableList<IDataBindItemModel>? = null

    /**
     * 重写Adapter各方法监听
     */
    var bindAdapterListener: BindAdapterListener? = null

    /**
     * 获得ItemModels
     */
    fun <T : IDataBindItemModel> getItemModels(): MutableList<T> {
        if (models == null) {
            models = mutableListOf()
        }
        return models as MutableList<T>
    }

    /**
     * 根据位置获得ItemModel
     */
    fun <T : IDataBindItemModel> getItemModel(position: Int): T? {
        return getItemModels<T>()[position]
    }

    /**
     * 设置单个ItemModel
     * @param additional 是否为追加数据
     */
    fun <T : IDataBindItemModel> setItemModel(model: T, position: Int = -1, additional: Boolean = false) {
        //旧数据临时变量
        val tempModels = if (bindAdapterListener != null) {
            mutableListOf(models) as MutableList<IDataBindItemModel>
        } else getItemModels()
        //逻辑操作
        if (additional) {
            if (position in 0 until getItemModels<T>().size) {
                getItemModels<T>().add(position, model)
                notifyItemInserted(position)
            } else {
                getItemModels<T>().add(model)
                notifyItemInserted(itemCount)
            }
        } else {
            if (position in 0 until itemCount) {
                getItemModels<T>()[position] = model
                notifyItemChanged(position)
            }
        }
        //通知回调
        bindAdapterListener?.onNotifyChange(tempModels, getItemModels())
    }

    /**
     * 移除ItemModel
     */
    fun removeItemModel(model: IDataBindItemModel): Int {
        getItemModels<IDataBindItemModel>().run {
            //旧数据临时变量
            val tempModels = if (bindAdapterListener != null) {
                mutableListOf(models) as MutableList<IDataBindItemModel>
            } else getItemModels()
            //移除操作
            val index = indexOf(model)
            removeAt(index)
            notifyItemRemoved(index)
            //移除itemModel通知回调
            bindAdapterListener?.onNotifyChange(tempModels, getItemModels())
            return index
        }
    }

    /**
     * 根据位置移除ItemModel
     */
    fun removeItemModel(index: Int): IDataBindItemModel? {
        //旧数据临时变量
        val tempModels = if (bindAdapterListener == null) {
            mutableListOf(models) as MutableList<IDataBindItemModel>
        } else getItemModels()
        //移除操作
        val model = getItemModels<IDataBindItemModel>().removeAt(index)
        notifyItemRemoved(index)
        //移除itemModel通知回调
        bindAdapterListener?.onNotifyChange(tempModels, getItemModels())
        return model
    }

    /**
     * 设置ItemModel列表，内部判断数据差异进行通知刷新界面
     */
    fun <T : IDataBindItemModel> setItemModels(newModels: MutableList<T>) {
        //对比新旧数据差异
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = itemCount
            override fun getNewListSize() = newModels.size

            //item类型是否相同
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldModel = getItemModels<T>()[oldItemPosition]
                val newModel = newModels[newItemPosition]
                return oldModel.diffId == newModel.diffId
            }

            //item内容是否相同
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldModel = getItemModels<T>()[oldItemPosition]
                val newModel = newModels[newItemPosition]
                return if (oldModel.sameContent() != null || newModel.sameContent() != null) {
                    oldModel.sameContent() == newModel.sameContent()
                } else {
                    oldModel == newModel
                }
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                //避免整个item出现闪一下动画
                return ""
            }
        })
        //通知item更新
        diffResult.dispatchUpdatesTo(this)
        //差异比较后通知回调
        bindAdapterListener?.onNotifyChange(getItemModels(), newModels as MutableList<IDataBindItemModel>)
        //将新数据覆盖旧数据
        models = newModels as MutableList<IDataBindItemModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), viewType, parent, false
        )
        bindAdapterListener?.onCreateViewHolder(binding)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindAdapterListener?.onBindViewHolderBefore(holder, position)
        val model = getItemModels<IDataBindItemModel>()[position]
        if (model.variableId > 0) {
            holder.binding.setVariable(model.variableId, model)
        }
        model.getVariablePairs()?.forEach {
            holder.binding.setVariable(it.first, it.second)
        }
        bindAdapterListener?.onBindViewHolderAfter(holder, position, model)
        //数据改变时立刻刷新UI
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return getItemModels<IDataBindItemModel>().size
    }

    override fun getItemViewType(position: Int): Int {
        return getItemModels<IDataBindItemModel>()[position].layoutRes
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (bindAdapterListener?.onBindViewHolder(holder, position, payloads) != true) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        bindAdapterListener?.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        bindAdapterListener?.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        bindAdapterListener?.onViewRecycled(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        bindAdapterListener?.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        bindAdapterListener?.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        val result = bindAdapterListener?.onFailedToRecycleView(holder)
        return result ?: super.onFailedToRecycleView(holder)
    }

    /****************************
     * ViewHolder、监听接口
     ****************************/

    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    interface BindAdapterListener {
        //onBindViewHolder数据绑定之前执行
        fun onBindViewHolderBefore(holder: ViewHolder, position: Int) {}

        //onBindViewHolder数据绑定之后执行
        fun onBindViewHolderAfter(holder: ViewHolder, position: Int, model: IDataBindItemModel) {}

        //需要处理局部刷新调用，返回true会中止后续onBindViewHolder(holder: ViewHolder, position: Int)的执行
        fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>): Boolean? = null

        //通知改变回调监听，原RecyclerView.AdapterDataObserver部分监听失效
        fun onNotifyChange(oldModels: MutableList<IDataBindItemModel>, newModels: MutableList<IDataBindItemModel>){}

        fun onCreateViewHolder(viewDataBinding: ViewDataBinding) {}
        fun onViewAttachedToWindow(holder: ViewHolder) {}
        fun onViewDetachedFromWindow(holder: ViewHolder) {}
        fun onViewRecycled(holder: ViewHolder) {}
        fun onFailedToRecycleView(holder: ViewHolder): Boolean? = null
        fun onAttachedToRecyclerView(recyclerView: RecyclerView) {}
        fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {}
    }
}