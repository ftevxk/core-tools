@file:Suppress("unused")

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
}

/*********************************
 * 扩展重置BindItemModelInfo信息
 *********************************/

/**
 * 重置item布局
 */
fun <T : IDataBindItemModel> T.resetLayoutRes(@LayoutRes layoutRes: Int): T {
    itemModelInfo.layoutRes = layoutRes
    return this
}

/**
 * 重置View对应绑定默认ViewModel的id
 */
fun <T : IDataBindItemModel> T.resetVariableId(variableId: Int): T {
    itemModelInfo.variableId = variableId
    return this
}

/**
 * 重置是否为同一条item的差异id
 */
fun <T : IDataBindItemModel> T.resetDiffId(diffId: Int): T {
    itemModelInfo.diffId = diffId
    return this
}

/**
 * 添加额外的VariableId绑定
 */
fun <T : IDataBindItemModel> T.addCustomData(vararg pairs: Pair<Int, Any>): T {
    itemModelInfo.customData = pairs.asList()
    return this
}

/**
 * 设置自定义ViewDataBinding
 */
fun <T : IDataBindItemModel, VDB : ViewDataBinding> T.setCustomBinding(customBinding: VDB): T {
    itemModelInfo.customBinding = customBinding
    return this
}