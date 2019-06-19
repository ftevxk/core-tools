package com.ftevxk.core.common

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
    val itemModelInfo: BindItemModelInfo

    /**
     * 相同内容，可供adapter数据差异对比
     * 默认不重写的话直接对比对象是否相等，非data class需要重写equals处理
     */
    fun sameContent(): Any? = null

    /**
     * 重置item布局
     */
    fun resetLayoutRes(@LayoutRes layoutRes: Int) {
        itemModelInfo.layoutRes = layoutRes
    }

    /**
     * 重置View对应绑定默认ViewModel的id
     */
    fun resetVariableId(variableId: Int) {
        itemModelInfo.variableId = variableId
    }

    /**
     * 重置是否为同一条item的差异id
     */
    fun resetDiffId(diffId: Int) {
        itemModelInfo.diffId = diffId
    }

    /**
     * 设置自定义ViewDataBinding
     */
    fun <T : ViewDataBinding> setCustomBinding(customBinding: T) {
        itemModelInfo.customBinding = customBinding
    }

    /**
     * 添加额外的VariableId绑定
     */
    fun addCustomData(vararg pairs: Pair<Int, Any>) {
        itemModelInfo.customData = pairs.asList()
    }
}