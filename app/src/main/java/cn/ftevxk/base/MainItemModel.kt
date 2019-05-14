package cn.ftevxk.base

import android.view.View
import cn.ftevxk.base.common.IDataBindItemModel

data class MainItemModel(
    override var diffId: Int = View.generateViewId(),
    override val layoutRes: Int = R.layout.item_main,
    override val variableId: Int = BR.model,
    var title: String = ""
) : IDataBindItemModel