package com.ftevxk.core.base

interface IBaseInitialize {
    fun initView() {}
    fun initData() {}
    fun initListener() {}
    fun getData(dataState: DataState) {}

    enum class DataState {
        //数据初始化请求
        DATA_INIT,

        //数据刷新请求
        DATA_REFRESH,

        //页面Resume数据请求
        DATA_RESUME,

        //页面追加数据
        DATA_ADDITIONAL
    }
}

/**
 * 是否是初始化或刷新状态
 */
fun IBaseInitialize.DataState.isInitOrRefresh(): Boolean {
    return this == IBaseInitialize.DataState.DATA_INIT ||
            this == IBaseInitialize.DataState.DATA_REFRESH
}