@file:Suppress("CommitPrefEdits", "UNCHECKED_CAST", "unused")

package com.ftevxk.base.extension

import android.content.Context
import androidx.fragment.app.Fragment
import org.json.JSONArray
import org.json.JSONObject

/**
 * 保存数据到SharedPreferences
 */
fun Context.putSpValue(vararg pairs: Pair<String, Any>, fileName: String = packageName) {
    val edit = getSharedPreferences(fileName, Context.MODE_PRIVATE).edit()
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
            is MutableSet<*> -> {
                if (value.firstOrNull() is String) {
                    edit.putStringSet(key, value as Set<String>)
                } else {
                    //Set类型不局限于String
                    val sets = mutableSetOf<String>()
                    value.forEach { sets.plus(anyToString(it)) }
                    edit.putStringSet(key, sets)
                }
            }
            is MutableList<*> -> {
                //List类型通过String间接存储
                val jsonArray = JSONArray()
                value.forEach { jsonArray.put(anyToString(it)) }
                edit.putString(key, jsonArray.toString())
            }
            is MutableMap<*, *> -> {
                //Map类型通过String间接存储
                val jsonObject = JSONObject()
                value.entries.forEach { jsonObject.put(anyToString(it.key), anyToString(it.value)) }
                edit.putString(key, jsonObject.toString())
            }
            else -> throw Throwable("当前类型不支持!!!")
        }
    }
    edit.apply()
}

/**
 * 从SharedPreferences获取数据
 */
fun <T> Context.getSpValue(key: String, default: T, fileName: String = packageName): T {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
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
        is MutableSet<*> -> {
            return if (default.firstOrNull() is String) {
                pref.getStringSet(key, default as Set<String>) as T
            } else {
                val result = mutableSetOf<Any?>()
                pref.getStringSet(key, setOf())?.forEach {
                    result.plus(stringToAny(it))
                }
                if (result.isEmpty()) default else result as T
            }
        }
        is MutableList<*> -> {
            //借助JSONArray将String转为List
            val string = pref.getString(key, "")
            return if (!string.isNullOrEmpty()) {
                val result = mutableListOf<Any?>()
                JSONArray(string).toMutableList<String>().forEach {
                    result.add(stringToAny(it))
                }
                result as T
            } else default
        }
        is MutableMap<*, *> -> {
            //借助JSONObject将String转为MutableMap
            val string = pref.getString(key, "")
            return if (!string.isNullOrEmpty()) {
                val result = mutableMapOf<Any?, Any?>()
                JSONObject(string).toMutableMap<String>().forEach {
                    result[stringToAny(it.key)] = stringToAny(it.value)
                }
                result as T
            } else default
        }
        else -> {
            throw IllegalArgumentException("当前类型不支持!!!")
        }
    }
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
fun Context.clearSpValue(fileName: String = packageName): Boolean {
    val pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
    return pref.edit().clear().commit()
}

/**********************************************************
 * (START) Fragment对应扩展
 * 需要在生命周期内调用，否则context可能为null
 **********************************************************/

fun Fragment.putSpValue(vararg pairs: Pair<String, Any>, fileName: String? = null) {
    context?.run {
        this.putSpValue(*pairs, fileName = fileName ?: this.packageName)
    }
}

fun <T> Fragment.getSpValue(key: String, default: T, fileName: String? = null): T {
    return context!!.getSpValue(key, default, fileName ?: context!!.packageName)
}

fun Fragment.removeSpKey(key: String, fileName: String? = null): Boolean {
    return context!!.removeSpKey(key, fileName ?: context!!.packageName)
}

fun Fragment.containsKey(key: String, fileName: String? = null): Boolean {
    return context!!.containsKey(key, fileName ?: context!!.packageName)
}

fun Fragment.clearSpValue(fileName: String? = null): Boolean {
    return context!!.clearSpValue(fileName ?: context!!.packageName)
}

/**********************************************************
 * (END) Fragment对应扩展
 **********************************************************/


/**********************************************************
 * (START) putSpValue/getSpValue中List、Map、Set相关处理
 * List、Map嵌套还未处理会返回null
 **********************************************************/

private fun getConvertRegex(type: String, value: String) =
    "RegexType=$type--RegexValue=$value"

private fun anyToString(any: Any?): String {
    return when (any) {
        is Int -> getConvertRegex("Int", any.toString())
        is Long -> getConvertRegex("Long", any.toString())
        is Float -> getConvertRegex("Float", any.toString())
        is Double -> getConvertRegex("Double", any.toString())
        is Boolean -> getConvertRegex("Boolean", any.toString())
        is JSONArray -> getConvertRegex("JsonArray", any.toString())
        is JSONObject -> getConvertRegex("JsonObject", any.toString())
        is String -> any
        else -> getConvertRegex("null", "null")
    }
}

private fun stringToAny(string: String?, default: Any? = null): Any? {
    if (string == null) return null
    val regex = Regex(getConvertRegex("(.*)", "(.*)"))
    return if (string.matches(regex)) {
        val groupValues = regex.find(string)?.groupValues
        if (groupValues != null) {
            when (groupValues[1]) {
                "Int" -> groupValues[2].toInt()
                "Long" -> groupValues[2].toLong()
                "Float" -> groupValues[2].toFloat()
                "Double" -> groupValues[2].toDouble()
                "Boolean" -> groupValues[2].toBoolean()
                "JsonArray" -> JSONArray(groupValues[2])
                "JsonObject" -> JSONObject(groupValues[2])
                else -> null
            }
        } else default
    } else string
}

/********************************************************
 * (END) putSpValue/getSpValue中List、Map、Set相关处理
 ********************************************************/