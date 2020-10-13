package com.ftevxk.example.viewmodel

import com.ftevxk.core.adapter.BindItemModelInfo
import com.ftevxk.core.adapter.IDataBindItemModel
import com.ftevxk.example.BR
import com.ftevxk.example.R

data class MainItemModel(var title: String = "") : IDataBindItemModel {
    override val bindItemModelInfo by lazy {
        BindItemModelInfo(R.layout.item_main, BR.model)
    }
}