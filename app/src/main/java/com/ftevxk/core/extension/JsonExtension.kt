@file:Suppress("UNCHECKED_CAST", "unused")

package com.ftevxk.core.extension

import org.json.JSONArray
import org.json.JSONObject

/**
 * 简单JSONArray转换成MutableList
 * @param isFloat Kotlin浮点型默认为Double是否要将其转换为Float
 */
fun <T> JSONArray.toMutableList(isFloat: Boolean = false): MutableList<T> {
    val list = mutableListOf<Any>()
    for (i in 0 until length()) {
        val value = this.get(i)
        if (isFloat && value is Double) {
            list.add(value.toFloat())
        } else {
            when(value){
                is JSONArray -> list.add(value.toMutableList<T>(isFloat))
                is JSONObject -> list.add(value.toMutableMap<T>(isFloat))
                else -> list.add(value)
            }
        }
    }
    return list as MutableList<T>
}

/**
 * 简单JSONObject转换成MutableMap
 * @param isFloat Kotlin浮点型默认为Double是否要将其转换为Float
 */
fun <T> JSONObject.toMutableMap(isFloat: Boolean = false): MutableMap<String, T> {
    val map = mutableMapOf<String, Any>()
    keys().forEach {
        val value = get(it)
        if (isFloat && value is Double) {
            map[it] = (value.toFloat())
        } else {
            when(value){
                is JSONArray -> map[it] = value.toMutableList<T>(isFloat)
                is JSONObject -> map[it] = value.toMutableMap<T>(isFloat)
                else -> map[it] = value
            }
        }
    }
    return map as MutableMap<String, T>
}

/**
 * Json格式打印
 */
fun String.toJsonPrint(): String {
    var level = 0
    val jsonForMatStr = StringBuffer()
    for (i in 0 until this.length) {
        val c = this[i]
        if (level > 0 && '\n' == jsonForMatStr[jsonForMatStr.length - 1]) {
            val levelStr = StringBuffer()
            for (levelI in 0 until level) {
                levelStr.append("\t")
            }
            jsonForMatStr.append(levelStr.toString())
        }
        when (c) {
            '{', '[' -> {
                jsonForMatStr.append(c + "\n")
                level++
            }
            ',' -> jsonForMatStr.append(c + "\n")
            '}', ']' -> {
                jsonForMatStr.append("\n")
                level--
                val levelStr = StringBuffer()
                for (levelI in 0 until level) {
                    levelStr.append("\t")
                }
                jsonForMatStr.append(levelStr.toString())
                jsonForMatStr.append(c)
            }
            else -> jsonForMatStr.append(c)
        }
    }
    return jsonForMatStr.toString()
}