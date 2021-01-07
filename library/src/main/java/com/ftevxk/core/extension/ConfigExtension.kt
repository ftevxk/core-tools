@file:Suppress("CommitPrefEdits", "UNCHECKED_CAST", "unused")

package com.ftevxk.core.extension

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

private const val SP_VALID_PERIOD = "sp_valid_period"

private var validPeriodPreference: SharedPreferences? = null

/**
 * 保存数据到SharedPreferences
 * @param validPeriod 有效期，传入到期时间戳，该有效期针对本次传入的所有
 */
fun Context.putSpStore(vararg pairs: Pair<String, Any>, validPeriod: Long = -1, fileName: String = packageName) {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    val edit = pref.edit()
    pairs.forEach { pair ->
        val key = pair.first
        when (val value = pair.second) {
            is Int -> edit.putInt(key, value)
            is Long -> edit.putLong(key, value)
            is Float -> edit.putFloat(key, value)
            is String -> edit.putString(key, value)
            is Boolean -> edit.putBoolean(key, value)
            //Double、JSON类型通过String间接存储
            is Double, is JSONObject, is JSONArray -> edit.putString(key, value.toString())
            is Set<*> -> {
                val sets = linkedSetOf<String>()
                value.forEach { sets.add(it.toString()) }
                edit.putStringSet(key, sets)
            }
            is List<*> -> {
                val sets = linkedSetOf<String>()
                value.forEach { sets.add(it.toString()) }
                edit.putStringSet(key, sets)
            }
            else -> throw Throwable("当前类型不支持!!!")
        }
        //设置
        setKeyValidPeriod(this, pref, key, validPeriod)
    }
    edit.apply()
}

/**
 * 设置带有效期的key
 */
private fun setKeyValidPeriod(context: Context, pref: SharedPreferences, key: String, validPeriod: Long) {
    if (validPeriod <= 0) return
    if (validPeriodPreference == null) {
        validPeriodPreference = context.getSharedPreferences(SP_VALID_PERIOD, Context.MODE_PRIVATE)
    }
    val edit = validPeriodPreference?.edit()
    //移除所有过期的key
    validPeriodPreference?.all?.forEach {
        val value = it.value
        if (value != null && value is Long && value <= System.currentTimeMillis()) {
            pref.edit().remove(it.key).apply()
            edit?.remove(it.key)
        }
    }
    edit?.putLong(key, validPeriod)?.apply()
}

/**
 * 从SharedPreferences获取数据
 */
fun <T> Context.getSpStore(key: String, default: T, fileName: String = packageName): T {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    if (isKeyOverDue(this, pref, key)) return default
    return when (default) {
        is Int -> pref.getInt(key, default) as T
        is Long -> pref.getLong(key, default) as T
        is Float -> pref.getFloat(key, default) as T
        is String -> pref.getString(key, default) as T
        is Boolean -> pref.getBoolean(key, default) as T
        //Double、JSON类型通过String间接读取
        is Double -> pref.getString(key, default.toString())!!.toDouble() as T
        is JSONArray -> {
            val jsonStr = pref.getString(key, "")
            return if (jsonStr.isNullOrEmpty()) default else JSONArray(jsonStr) as T
        }
        is JSONObject -> {
            val jsonStr = pref.getString(key, "")
            return if (jsonStr.isNullOrEmpty()) default else JSONObject(jsonStr) as T
        }
        is Set<*> -> {
            val defaultSets = linkedSetOf<String>()
            default.forEach { defaultSets.add(it.toString()) }
            val stringSets = pref.getStringSet(key, defaultSets)
            return when (default) {
                is HashSet<*> -> {
                    val sets = hashSetOf<Any>()
                    stringSets?.forEach { sets.add(it) }
                    sets as T
                }
                is TreeSet<*> -> {
                    val sets = sortedSetOf<Any>()
                    stringSets?.forEach { sets.add(it) }
                    sets as T
                }
                is MutableSet<*> -> {
                    val sets = mutableSetOf<Any>()
                    stringSets?.forEach { sets.add(it) }
                    sets as T
                }
                is LinkedHashSet<*> -> stringSets as T
                else -> {
                    val sets = setOf<Any>()
                    stringSets?.forEach { sets.plus(it) }
                    sets as T
                }
            }
        }
        is List<*> -> {
            val defaultSets = linkedSetOf<String>()
            default.forEach { defaultSets.add(it.toString()) }
            val stringSets = pref.getStringSet(key, defaultSets)
            return when (default) {
                is MutableList<*> -> {
                    val list = mutableListOf<Any>()
                    stringSets?.forEach { list.add(it) }
                    list as T
                }
                is ArrayList<*> -> {
                    val list = arrayListOf<Any>()
                    stringSets?.forEach { list.add(it) }
                    list as T
                }
                else -> {
                    val list = listOf<Any>()
                    stringSets?.forEach { list.plus(it) }
                    list as T
                }
            }
        }
        else -> {
            throw IllegalArgumentException("当前类型不支持!!!")
        }
    }
}

/**
 * 判断Key是否已经过期了
 */
private fun isKeyOverDue(context: Context, pref: SharedPreferences, key: String): Boolean {
    if (validPeriodPreference == null) {
        validPeriodPreference = context.getSharedPreferences(SP_VALID_PERIOD, Context.MODE_PRIVATE)
    }
    //判断是否存在有效期且已过期
    if (validPeriodPreference?.contains(key) == true) {
        if (validPeriodPreference?.getLong(key, 0) ?: 0 <= System.currentTimeMillis()) {
            pref.edit().remove(key).apply()
            validPeriodPreference?.edit()?.remove(key)?.apply()
            return true
        }
    }
    return !pref.contains(key)
}

/**
 * 移除指定key
 */
fun Context.removeSpKey(key: String, fileName: String = packageName): Boolean {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return pref.edit().remove(key).commit()
}

/**
 * 是否存在Key
 */
fun Context.containsKey(key: String, fileName: String = packageName): Boolean {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return pref.contains(key)
}

/**
 * 清空
 */
fun Context.clearSpStore(fileName: String = packageName): Boolean {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return pref.edit().clear().commit()
}

/**********************************************************
 * (START) Fragment对应扩展，需要在生命周期内调用
 **********************************************************/

fun Fragment.putSpStore(vararg pairs: Pair<String, Any>, validPeriod: Long = -1, fileName: String? = null) {
    context?.run {
        this.putSpStore(*pairs, validPeriod = validPeriod, fileName = fileName ?: this.packageName)
    }
}

fun <T> Fragment.getSpStore(key: String, default: T, fileName: String? = null): T {
    return context?.let {
        it.getSpStore(key, default, fileName ?: it.packageName)
    } ?: default
}

fun Fragment.removeSpKey(key: String, fileName: String? = null): Boolean {
    return context?.let {
        it.removeSpKey(key, fileName ?: it.packageName)
    } ?: false
}

fun Fragment.containsKey(key: String, fileName: String? = null): Boolean {
    return context?.let {
        it.containsKey(key, fileName ?: it.packageName)
    } ?: false
}

fun Fragment.clearSpStore(fileName: String? = null): Boolean {
    return context?.let {
        it.clearSpStore(fileName ?: it.packageName)
    } ?: false
}

/**********************************************************
 * (END) Fragment对应扩展
 **********************************************************/