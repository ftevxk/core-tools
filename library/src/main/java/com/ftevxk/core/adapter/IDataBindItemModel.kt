@file:Suppress("unused")

package com.ftevxk.core.adapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

/**
 * DataBindAdapter配套使用的ItemModel
 * Created by ftevxk on 19-04-26.
 */
interface IDataBindItemModel {

    /**
     * 设置layoutRes、variableId等信息
     */
    val bindItemModelInfo: BindItemModelInfo

    /**
     * 相同内容，可供adapter数据差异对比
     * 默认不重写的话直接对比对象是否相等，非data class需要重写equals处理
     */
    fun sameContent(): Any? = null

    /**
     * model数据更改通知更新
     */
    fun notifyUpdateModel() {
        bindItemModelInfo.modelUpdateListener?.invoke(this)
    }

    /**
     * adapter的onCreateViewHolder回调
     */
    fun onCreateViewHolder(holder: DataBindAdapter.BindViewHolder, position: Int) {

    }

    /**
     * adapter的onBindViewHolder回调
     */
    fun onBindViewHolder(holder: DataBindAdapter.BindViewHolder, position: Int) {

    }

    /**
     * adapter的onViewAttachedToWindow回调
     */
    fun onViewAttachedToWindow(holder: DataBindAdapter.BindViewHolder) {
        bindItemModelInfo.modelUpdateListener = {
            holder.binding.setVariable(bindItemModelInfo.variableId, it)
            holder.binding.executePendingBindings()
        }
    }

    /**
     * adapter的onViewDetachedFromWindow回调
     */
    fun onViewDetachedFromWindow(holder: DataBindAdapter.BindViewHolder) {
        bindItemModelInfo.modelUpdateListener = null
    }
}

/*********************************
 * BindItemModelInfo
 *********************************/

/**
 * DataBindAdapter操作的Item信息类
 * @param layoutRes         item布局
 * @param variableId        View对应绑定默认ViewModel的id，BR文件类似R文件由系统统一维护, 格式: 包名+BR+id
 * @param diffId            判断是否为同一条item的差异id, 可用View.generateViewId()保证唯一
 * @param customBinding     传入自定义的ViewDataBinding，跳过layoutRes
 * @param customData        自定义额外的VariableId绑定
 */
data class BindItemModelInfo(
        @LayoutRes var layoutRes: Int,
        var variableId: Int,
        var diffId: Int = View.generateViewId(),
        var customBinding: ViewDataBinding? = null,
        var customData: List<Pair<Int, Any>>? = null
) {
    internal var modelUpdateListener: ((model: IDataBindItemModel) -> Unit)? = null
}

/**
 * 重置item布局
 */
fun <T : IDataBindItemModel> T.resetLayoutRes(@LayoutRes layoutRes: Int): T {
    bindItemModelInfo.layoutRes = layoutRes
    return this
}

/**
 * 重置View对应绑定默认ViewModel的id
 */
fun <T : IDataBindItemModel> T.resetVariableId(variableId: Int): T {
    bindItemModelInfo.variableId = variableId
    return this
}

/**
 * 重置是否为同一条item的差异id
 */
fun <T : IDataBindItemModel> T.resetDiffId(diffId: Int): T {
    bindItemModelInfo.diffId = diffId
    return this
}

/**
 * 添加额外的VariableId绑定
 */
fun <T : IDataBindItemModel> T.addCustomData(vararg pairs: Pair<Int, Any>): T {
    bindItemModelInfo.customData = pairs.asList()
    return this
}

/**
 * 设置自定义ViewDataBinding
 */
fun <T : IDataBindItemModel, VDB : ViewDataBinding> T.setCustomBinding(customBinding: VDB): T {
    bindItemModelInfo.customBinding = customBinding
    return this
}