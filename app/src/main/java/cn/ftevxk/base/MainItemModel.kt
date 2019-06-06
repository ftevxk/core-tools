package cn.ftevxk.base

import android.view.View
import cn.ftevxk.base.common.IDataBindItemModel

data class MainItemModel(var title: String = "") : IDataBindItemModel {
    override var diffId: Int = View.generateViewId()
    override var layoutRes: Int = R.layout.item_main
    override var variableId: Int = BR.model
}