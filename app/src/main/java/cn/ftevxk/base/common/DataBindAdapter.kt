@file:Suppress("UNCHECKED_CAST")

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

    private val models by lazy {
        mutableListOf<IDataBindItemModel>()
    }

    fun <T : IDataBindItemModel> getItemModels() = models as MutableList<T>

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
                val oldModel = models[oldItemPosition]
                val newModel = newModels[newItemPosition]
                return oldModel.diffId == newModel.diffId
            }

            //item内容是否相同
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldModel = models[oldItemPosition]
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
        //将新数据覆盖旧数据
        models.clear()
        models.addAll(newModels)
    }

    /**
     * 设置单个ItemModel
     * @param additional 是否为追加数据
     */
    fun <T : IDataBindItemModel> setItemModel(model: T, position: Int = -1, additional: Boolean = false) {
        val tempModels = mutableListOf<T>()
        tempModels.addAll(models as MutableList<T>)
        if (!additional && position in 0 until models.size) {
            tempModels[position] = model
        } else {
            tempModels.add(model)
        }
        setItemModels(tempModels)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        if (model.variableId > 0) {
            holder.binding.setVariable(model.variableId, model)
        }
        model.getVariablePairs()?.forEach {
            holder.binding.setVariable(it.first, it.second)
        }
        //数据改变时立刻刷新UI
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = models.size

    override fun getItemViewType(position: Int): Int {
        return models[position].layoutRes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context), viewType, parent, false
        )
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}