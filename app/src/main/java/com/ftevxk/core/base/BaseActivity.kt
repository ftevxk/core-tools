package com.ftevxk.core.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.ftevxk.core.extension.tryCatchLog
import com.ftevxk.example.R

abstract class BaseActivity(
        private var portraitScreen: Boolean = true
) : AppCompatActivity(), IBaseInitialize {

    companion object {
        val activityStacks by lazy {
            mutableListOf<BaseActivity>()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //代码固定坚屏显示，避免Android O版本透明固定崩溃
        if (portraitScreen) {
            tryCatchLog {
                //一定要放到try catch里面，否则会崩溃
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        //全局activity切换动画
        overridePendingTransition(R.anim.activity_page_in, R.anim.activity_page_out)
        super.onCreate(savedInstanceState)
        activityStacks.add(this)
    }

    override fun finish() {
        super.finish()
        //全局activity切换动画
        overridePendingTransition(R.anim.activity_page_in, R.anim.activity_page_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityStacks.remove(this)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        initView()
        initData()
        initListener()
        getData(IBaseInitialize.DataState.DATA_INIT)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initView()
        initData()
        initListener()
        getData(IBaseInitialize.DataState.DATA_INIT)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        initView()
        initData()
        initListener()
        getData(IBaseInitialize.DataState.DATA_INIT)
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseFragment) {
                fragment.onActivityResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is BaseFragment) {
                fragment.onActivityPause()
            }
        }
    }

    /**
     * 添加Fragment
     */
    fun addFragment(@IdRes rootViewRes: Int, showIndex: Int?, vararg fragments: Fragment) {
        supportFragmentManager.beginTransaction().apply {
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
        supportFragmentManager.beginTransaction().apply {
            if (index in 0 until supportFragmentManager.fragments.size) {
                for (i in 0 until supportFragmentManager.fragments.size) {
                    val fragment = supportFragmentManager.fragments[i]
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
        supportFragmentManager.beginTransaction().apply {
            for (fragment in supportFragmentManager.fragments) {
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
     * 接收来自fragment的通知
     */
    open fun <T> receiveNotify(data: T) {}

    /**
     * 通知fragment
     */
    inline fun <reified T> notifyFragment(index: Int, data: T) {
        (supportFragmentManager.fragments[index] as?
                BaseFragment)?.receiveNotify(data)
    }
}