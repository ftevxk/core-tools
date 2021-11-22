package com.ftevxk.example

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.multidex.MultiDex
import com.ftevxk.core.extension.getSpStore
import com.ftevxk.core.extension.putSpStore
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


class MainApplication : Application() {

    companion object {
        lateinit var application: MainApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        //初始化Logger
        initLogger()
        //初始化刷新框架
        initSmartRefreshLayout()
    }

    /**
     * 初始化Logger
     */
    private fun initLogger() {
        Logger.addLogAdapter(
                AndroidLogAdapter(
                        PrettyFormatStrategy.newBuilder()
                                .showThreadInfo(false)
                                .methodCount(5)
                                .build()
                )
        )
    }

    /**
     * 初始化刷新框架
     */
    private fun initSmartRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }
    }
}

val app get() = MainApplication.application
val mainHandler by lazy { Handler(Looper.getMainLooper()) }

/**
 * 显示短吐司
 */
fun toastShort(msg: CharSequence?): Toast? {
    if (msg.isNullOrEmpty()) return null
    return Toast.makeText(
            MainApplication.application,
            msg, Toast.LENGTH_SHORT
    ).apply { mainHandler.post { show() } }
}

/**
 * 显示长吐司
 */
fun toastLong(msg: CharSequence?): Toast? {
    if (msg.isNullOrEmpty()) return null
    return Toast.makeText(
            MainApplication.application,
            msg, Toast.LENGTH_LONG
    ).apply { mainHandler.post { show() } }
}

/**
 * 全局存储SP数据
 */
fun putSpStore(
        vararg pairs: Pair<String, Any>,
        validPeriod: Long = -1L,
        fileName: String = MainApplication.application.packageName
) {
    MainApplication.application.putSpStore(
            *pairs, validPeriod = validPeriod, fileName = fileName
    )
}

/**
 * 全局读取SP数据
 */
inline fun <reified T> getSpStore(
        key: String,
        default: T,
        fileName: String = MainApplication.application.packageName
): T {
    return MainApplication.application.getSpStore(key, default, fileName)
}