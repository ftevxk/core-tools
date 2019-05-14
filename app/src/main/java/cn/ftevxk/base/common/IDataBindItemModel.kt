package cn.ftevxk.base.common

/**
 * DataBindAdapter配套使用的ItemModel
 * Created by ftevxk on 19-04-26.
 */
interface IDataBindItemModel {

    /**
     * 判断是否为同一条item的差异id
     * 可用View.generateViewId()保证唯一
     */
    var diffId: Int

    /**r
     * item布局
     */
    val layoutRes: Int

    /**
     * View对应绑定默认ViewModel的ID，BR文件类似R文件由系统统一维护, 格式: 包名+BR+ID
     */
    val variableId: Int

    /**
     * 有额外的VariableId需要绑定可重写
     */
    fun getVariablePairs(): List<Pair<Int, Any>>? = null

    /**
     * 相同内容，可供adapter数据差异对比
     * 默认不重写的话直接对比对象是否相等，非data class需要重写equals处理
     */
    fun sameContent(): Any? = null
}