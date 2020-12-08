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
 * 设置是否开启预加载
 */
fun ViewPager2.setItemPrefetchEnabled(enable: Boolean) {
    (this.getChildAt(0) as RecyclerView).layoutManager?.isItemPrefetchEnabled = enable
}

/****************************************************************
 * ViewPager2绑定Adapter设定联动TabLayout
 ****************************************************************/

fun ViewPager2.bindFragmentStateAdapter(baseActivity: BaseActivity, fragments: List<Fragment>,
                                        tabLayout: TabLayout?, smoothScroll: Boolean = true, tabs: List<String>? = null) {
    bindFragmentStateAdapter(baseActivity.lifecycle, baseActivity.supportFragmentManager, fragments, tabLayout, smoothScroll, tabs)
}

fun ViewPager2.bindFragmentStateAdapter(baseFragment: BaseFragment, fragments: List<Fragment>,
                                        tabLayout: TabLayout?, smoothScroll: Boolean = true, tabs: List<String>? = null) {
    bindFragmentStateAdapter(baseFragment.lifecycle, baseFragment.childFragmentManager, fragments, tabLayout, smoothScroll, tabs)
}

fun ViewPager2.bindFragmentStateAdapter(lifecycle: Lifecycle, fragmentManager: FragmentManager,
                                        fragments: List<Fragment>, tabLayout: TabLayout?, smoothScroll: Boolean, tabs: List<String>?) {
    adapter = object : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
    if (tabLayout != null) {
        if (tabs == null) {
            val tabList = arrayListOf<TabLayout.Tab>()
            for (i in 0 until tabLayout.tabCount) {
                tabList.add(copyTab(tabLayout.getTabAt(i), tabLayout.newTab()))
            }
            TabLayoutMediator(tabLayout, this) { tab, position ->
                copyTab(tabList[position], tab)
            }.attach()
        } else {
            TabLayoutMediator(tabLayout, this) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
        if (!smoothScroll) {
            tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }

            })
        }
    }
}

/**
 * 拷贝TabLayout.Tab
 */
private fun copyTab(rawTab: TabLayout.Tab?, newTab: TabLayout.Tab): TabLayout.Tab {
    if (rawTab != null) {
        newTab.setTag(rawTab.tag)
                .setIcon(rawTab.icon)
                .setText(rawTab.text)
                .setCustomView(rawTab.customView)
                .setTabLabelVisibility(rawTab.tabLabelVisibility)
                .apply {
                    view = rawTab.view
                    parent = rawTab.parent
                    contentDescription = rawTab.contentDescription
                }
    }
    return newTab
}

@BindingAdapter("bind_bottom_navigation")
fun ViewPager2.bindBottomNavigationView(bottomNavigationView: BottomNavigationView) {
    bindBottomNavigationView(bottomNavigationView, false)
}


/****************************************************************
 * ViewPager2绑定联动BottomNavigationView
 ****************************************************************/

fun ViewPager2.bindBottomNavigationView(bottomNavigationView: BottomNavigationView, smoothScroll: Boolean) {
    val arrays = SparseIntArray()
    val menuSize = bottomNavigationView.menu.size()
    if (menuSize > 0) {
        for (i in 0 until menuSize) {
            arrays.put(i, bottomNavigationView.menu.getItem(i).itemId)
        }
        bottomNavigationView.selectedItemId = arrays[0]
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val index = arrays.indexOfValue(menuItem.itemId)
            if (index >= 0) {
                this.setCurrentItem(arrays.keyAt(index), smoothScroll)
                return@setOnNavigationItemSelectedListener true
            } else {
                return@setOnNavigationItemSelectedListener false
            }
        }
        this.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val itemId = arrays[position]
                if (itemId >= 0) {
                    bottomNavigationView.selectedItemId = itemId
                }
            }
        })
    }
}