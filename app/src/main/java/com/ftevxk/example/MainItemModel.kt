package com.ftevxk.example

import com.ftevxk.core.adapter.BindItemModelInfo
import com.ftevxk.core.adapter.IDataBindItemModel

data class MainItemModel(var title: String = "") : IDataBindItemModel {
    override val bindItemModelInfo by lazy {
        BindItemModelInfo(R.layout.item_main, BR.model)
    }
}