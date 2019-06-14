package com.ftevxk.example

import android.view.View
import com.ftevxk.core.common.IDataBindItemModel

data class MainItemModel(var title: String = "") : IDataBindItemModel {
    override var diffId: Int = View.generateViewId()
    override var layoutRes: Int = R.layout.item_main
    override var variableId: Int = BR.model
}