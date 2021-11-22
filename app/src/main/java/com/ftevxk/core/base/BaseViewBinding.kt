package com.ftevxk.core.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

interface BaseViewBinding<Binding : ViewDataBinding> {
    val binding: Binding

    val baseActivity: BaseActivity?

    val baseFragment: BaseFragment?

    fun getTargetActivity(): BaseActivity {
        return baseActivity ?: baseFragment!!.activity as BaseActivity
    }

    fun getTargetContext(): Context {
        return baseFragment?.context ?: baseActivity!!
    }

    fun getTargetLifecycleOwner(): LifecycleOwner {
        return baseActivity ?: baseFragment!!
    }

    fun getTargetFragmentManager(): FragmentManager {
        return baseActivity?.supportFragmentManager ?: baseFragment!!.childFragmentManager
    }
}