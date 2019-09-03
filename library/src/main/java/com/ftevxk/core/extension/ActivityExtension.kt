@file:Suppress("UNUSED_VARIABLE", "unused")

package com.ftevxk.core.extension

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ftevxk.core.fragment.ResultFragment
import org.jetbrains.anko.internals.AnkoInternals

fun FragmentActivity.startActivityForResult(intent: Intent, result: (Intent?) -> Unit){
    val fragment = ResultFragment(intent){ fragment, it ->
        result.invoke(it)
        supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }
    supportFragmentManager.beginTransaction().add(0, fragment).commitAllowingStateLoss()
}

inline fun <reified T: Activity> FragmentActivity.startActivityForResult(vararg params: Pair<String, Any?>, noinline result: (Intent?) -> Unit){
    val intent = AnkoInternals.createIntent(this, T::class.java, params)
    val fragment = ResultFragment(intent){ fragment, it ->
        result.invoke(it)
        supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }
    supportFragmentManager.beginTransaction().add(0, fragment).commitAllowingStateLoss()
}

inline fun <reified T: Activity> Fragment.startActivityForResult(vararg params: Pair<String, Any?>, noinline result: (Intent?) -> Unit){
    val intent = AnkoInternals.createIntent(activity!!, T::class.java, params)
    val fragment = ResultFragment(intent){ fragment, it ->
        result.invoke(it)
        childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }
    childFragmentManager.beginTransaction().add(0, fragment).commitAllowingStateLoss()
}

fun FragmentActivity.finish(vararg params: Pair<String, Any?>){
    val intent = AnkoInternals.createIntent(this, javaClass, params)
    setResult(Activity.RESULT_OK, intent)
    finish()
}