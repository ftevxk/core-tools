@file:Suppress("unused")

package com.ftevxk.core.extension

import android.content.ClipData
import android.content.Context
import org.jetbrains.anko.clipboardManager
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * 返回不为Null的String串
 * @param replace 当为Null时的替代String
 */
fun String?.getNotNullStr(vararg replace: String?): String {
    if (!this.isNullOrBlank()) {
        return this
    }
    replace.forEach {
        if (!it.isNullOrBlank()) {
            return it
        }
    }
    return ""
}

/**
 * 返回指定格式时间 - String转Date
 * @param format yyyy(年) MM(月) dd(日) HH(时) mm(分) ss(秒) EEEE(星期：星期X) EEE(星期：周X)
 */
fun String.getFormatDate(format: String = "yyyy-MM-dd", locale: Locale = Locale.CHINA): Date? {
    return SimpleDateFormat(format, locale).parse(this)
}

/**
 * 返回指定格式时间 - Date转String
 * @param format yyyy(年) MM(月) dd(日) HH(时) mm(分) ss(秒) EEEE(星期：星期X) EEE(星期：周X)
 */
fun Any.getFormatString(format: String = "yyyy-MM-dd", locale: Locale = Locale.CHINA): String {
    return SimpleDateFormat(format, locale).format(if (this is Date) this else Date())
}

/**
 * 字符串md5加密
 */
fun String.md5(emptyMd5: Boolean = true): String {
    if (!emptyMd5 && this.isEmpty()) {
        return ""
    }
    return this.toByteArray().md5()
}

/**
 * 字节数组md5加密
 */
fun ByteArray.md5(): String {
    val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
    val digest: ByteArray = instance.digest(this)//对字符串加密，返回字节数组
    val sb = StringBuffer()
    for (b in digest) {
        val i: Int = b.toInt() and 0xff//获取低八位有效值
        var hexString = Integer.toHexString(i)//将整数转化为16进制
        if (hexString.length < 2) {
            hexString = "0$hexString"//如果是一位的话，补0
        }
        sb.append(hexString)
    }
    return sb.toString()
}

/**
 * 将String分割成指定大小的数组
 */
fun String.splitStr(maxLength: Int = 4 * 1024): Array<String> {
    var start = 0
    val strings = Array(this.length / maxLength + 1) { "" }
    for (i in strings.indices) {
        if (start + maxLength < this.length) {
            strings[i] = this.substring(start, start + maxLength)
            start += maxLength
        } else {
            strings[i] = this.substring(start, this.length)
            start = this.length
        }
    }
    return strings
}

/**
 * 简单复制文本到剪切板
 */
fun String.copyToClipboard(context: Context, label: String = "label"): Boolean {
    return try {
        context.clipboardManager.setPrimaryClip(ClipData.newPlainText(label, this))
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}