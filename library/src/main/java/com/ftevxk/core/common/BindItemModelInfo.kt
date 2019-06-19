package com.ftevxk.core.common

import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

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
)