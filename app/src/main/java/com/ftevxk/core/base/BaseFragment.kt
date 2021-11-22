package com.ftevxk.core.base

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

abstract class BaseFragment : Fragment(), IBaseInitialize {

    private var isInitData = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        if (isInitData) {
            //懒初始化获取数据
            getData(IBaseInitialize.DataState.DATA_INIT)
            isInitData = false
        }
    }

    open fun onActivityResume() {
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseFragment) {
                fragment.onActivityResume()
            }
        }
    }

    open fun onActivityPause() {
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseFragment) {
                fragment.onActivityPause()
            }
        }
    }

    /**
     * 添加Fragment
     */
    fun addFragment(@IdRes rootViewRes: Int, showIndex: Int?, vararg fragments: Fragment) {
        childFragmentManager.beginTransaction().apply {
            for (i in fragments.indices) {
                add(rootViewRes, fragments[i], fragments[i]::class.java.simpleName)
                if (i == showIndex) {
                    show(fragments[i])
                    setMaxLifecycle(fragments[i], Lifecycle.State.RESUMED)
                } else {
                    hide(fragments[i])
                    setMaxLifecycle(fragments[i], Lifecycle.State.STARTED)
                }
            }
        }.commitAllowingStateLoss()
    }

    /**
     * 显示Fragment
     */
    fun showFragment(index: Int) {
        childFragmentManager.beginTransaction().apply {
            if (index in 0 until childFragmentManager.fragments.size) {
                for (i in 0 until childFragmentManager.fragments.size) {
                    val fragment = childFragmentManager.fragments[i]
                    if (i == index) {
                        show(fragment)
                        setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    } else {
                        hide(fragment)
                        setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                    }
                }
            }
        }.commitAllowingStateLoss()
    }

    /**
     * 显示Fragment
     */
    fun showFragment(showFragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            for (fragment in childFragmentManager.fragments) {
                if (fragment == showFragment) {
                    show(fragment)
                    setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                } else {
                    hide(fragment)
                    setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                }
            }
        }.commitAllowingStateLoss()
    }

    /**
     * 接收来自activity的通知
     */
    open fun <T> receiveNotify(data: T) {}

    /**
     * 通知activity
     */
    inline fun <reified T> notifyActivity(data: T) {
        (activity as? BaseActivity)?.receiveNotify(data)
    }

    /**
     * 通知fragment
     */
    inline fun <reified T> notifyFragment(index: Int, data: T) {
        (childFragmentManager.fragments[index] as?
                BaseFragment)?.receiveNotify(data)
    }
}