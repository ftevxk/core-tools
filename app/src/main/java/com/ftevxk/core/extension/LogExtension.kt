@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.ftevxk.core.extension

import com.ftevxk.example.BuildConfig
import com.orhanobut.logger.Logger
import org.json.JSONArray
import org.json.JSONObject

inline fun vLog(
    msg: Any?, tag: String = "LoggerDebug",
    prefix: String = "", suffix: String = ""
) {
    disposeMsgInfo(msg, prefix, suffix) { text ->
        Logger.t(tag).v(text)
    }
}

inline fun dLog(
    msg: Any?, tag: String = "LoggerDebug",
    prefix: String = "", suffix: String = ""
) {
    disposeMsgInfo(msg, prefix, suffix) { text ->
        Logger.t(tag).d(text)
    }
}

inline fun iLog(
    msg: Any?, tag: String = "LoggerDebug",
    prefix: String = "", suffix: String = ""
) {
    disposeMsgInfo(msg, prefix, suffix) { text ->
        Logger.t(tag).i(text)
    }
}

inline fun wLog(
    msg: Any?, tag: String = "LoggerDebug",
    prefix: String = "", suffix: String = ""
) {
    disposeMsgInfo(msg, prefix, suffix) { text ->
        Logger.t(tag).w(text)
    }
}

inline fun eLog(
    msg: Any?, tag: String = "LoggerDebug",
    prefix: String = "", suffix: String = ""
) {
    disposeMsgInfo(msg, prefix, suffix) { text ->
        Logger.t(tag).e(text)
    }
}

/**
 * 处理JSON、XML、前缀、后缀信息
 */
inline fun disposeMsgInfo(
    msg: Any?, prefix: String, suffix: String,
    result: (text: String) -> Unit
) {
    //不为null且在DEBUG模式下才打印日志
    if (msg != null && BuildConfig.DEBUG) {
        if (msg is String) {
            var text = msg.toString()
            if (text.startsWith("{") && text.endsWith("}")) {
                //JSONObject输出打印格式化
                text = JSONObject(text).toString(2)
            } else if (text.startsWith("[") && text.endsWith("]")) {
                //JSONArray输出打印格式化
                text = JSONArray(text).toString(2)
            }
//            else if (text.startsWith("<") && text.endsWith(">")) {
//                //XML输出打印格式化
//                val xmlInput = StreamSource(StringReader(text))
//                val xmlOutput = StreamResult(StringWriter())
//                val transformer = TransformerFactory.newInstance().newTransformer()
//                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
//                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
//                transformer.transform(xmlInput, xmlOutput)
//                text = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n")
//            }
            //添加前后缀并返回处理后的数据
            if (prefix.isNotEmpty()) {
                text = "$prefix\n$text"
            }
            if (suffix.isNotEmpty()) {
                text = "$text\n$suffix"
            }
            result.invoke(text)
        } else {
            //添加前后缀并返回处理后的数据
            var text = msg.toString()
            if (prefix.isNotEmpty()) {
                text = "$prefix\n$text"
            }
            if (suffix.isNotEmpty()) {
                text = "$text\n$suffix"
            }
            result.invoke(text)
        }
    }
}