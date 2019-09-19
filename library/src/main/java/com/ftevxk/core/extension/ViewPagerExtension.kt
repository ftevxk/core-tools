@file:Suppress("DEPRECATION", "unused", "LiftReturnOrAssignment")

package com.ftevxk.core.extension

import android.util.SparseIntArray
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.forEachChild

/**
 * 便捷绑定FragmentPagerAdapter
 * @param titles tab显示对应标题，默认查找使用XML布局中TabLayout下TabItem的text
 * @param fragmentManager 默认自动判断获取当前View属于FragmentActivity还是Fragment进行设置
 */
fun ViewPager.bindFragmentAdapter(fragments: List<Fragment>,
                                  titles: List<CharSequence?>? = null,
                                  fragmentManager: FragmentManager? = null) {
    val host = this.findViewHost()
    val newFragmentManager = fragmentManager ?: when (host) {
        is FragmentActivity -> host.supportFragmentManager
        is Fragment -> host.childFragmentManager
        else -> null
    }
    val newTitles = titles ?: getXmlTabItemTexts()
    this.adapter = object : FragmentPagerAdapter(newFragmentManager){
        override fun getCount() = fragments.size
        override fun getItem(position: Int) = fragments[position]
        override fun getPageTitle(position: Int) = newTitles?.get(position)
    }
}

/**
 * FragmentPagerAdapter默认会覆盖XML布局ViewPager下的TabLayout
 * 当前方法可在设置FragmentPagerAdapter前将TabItem的text提取出来
 */
fun ViewPager.getXmlTabItemTexts(): List<CharSequence?>?{
    var tabLayout : TabLayout? = null
    this.forEachChild {
        if (it is TabLayout){
            tabLayout = it
            return@forEachChild
        }
    }
    if (tabLayout != null && tabLayout!!.tabCount > 0){
        val titles = mutableListOf<CharSequence?>()
        for (i in 0 until tabLayout!!.tabCount) {
            titles.add(tabLayout!!.getTabAt(i)?.text)
        }
        return titles
    }else{
        return null
    }
}

/**
 * ViewPager绑定联动BottomNavigationView
 */
@BindingAdapter("tools:bind_bottom_navigation")
fun ViewPager.bindBottomNavigationView(bottomNavigationView: BottomNavigationView){
    val arrays = SparseIntArray()
    for (i in 0 until bottomNavigationView.menu.size()){
        arrays.put(i, bottomNavigationView.menu.getItem(i).itemId)
    }
    bottomNavigationView.selectedItemId = arrays[0]
    bottomNavigationView.setOnNavigationItemSelectedListener {
        val index = arrays.indexOfValue(it.itemId)
        if (index >= 0){
            this.setCurrentItem(arrays.keyAt(index), false)
            return@setOnNavigationItemSelectedListener true
        }else{
            return@setOnNavigationItemSelectedListener false
        }
    }
    this.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener(){
        override fun onPageSelected(position: Int) {
            val value = arrays[position]
            if (value >= 0) bottomNavigationView.selectedItemId = value
        }
    })
}