package com.ftevxk.example

import com.ftevxk.core.common.BindItemModelInfo
import com.ftevxk.core.common.IDataBindItemModel

data class MainItemModel(var title: String = "") : IDataBindItemModel {
    override val itemModelInfo by lazy {
        BindItemModelInfo(R.layout.item_main, BR.model)
    }
}