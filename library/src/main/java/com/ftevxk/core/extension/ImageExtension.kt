package com.ftevxk.core.extension

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

//图片全局错误图
var globalImageError: (@DrawableRes Int)? = null

//图片全局占位图
var globalImagePlaceHolder: (@DrawableRes Int)? = null

//图片全局缩略图比例
var globalImageThumbnail: Float = 0f

private val defaultOptions by lazy {
    RequestOptions().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
}

/**
 * 使用Okhttp作为底层网络通信库
 */
@GlideModule
class OkHttpGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val builder = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(builder.build()))
    }
}

/**
 * 图片加载
 * placeholder和error仅支持Drawable或@DrawableRes
 * transform和circle仅有一个，优先transform
 * @param res 任意图片资源
 */
@BindingAdapter(value = ["res", "placeholder", "error",
    "asBitmap", "transform", "circle", "diskCache", "thumbnail"], requireAll = false)
fun ImageView.load(res: Any?, placeholder: Any? = null, error: Any? = null,
                   asBitmap: Boolean = false, transform: Transformation<Bitmap>? = null,
                   circle: Boolean = false, diskCache: Boolean = true, thumbnail: Float = globalImageThumbnail) {
    this.load(res, {
        var options = RequestOptions()
        when (placeholder) {
            is Drawable -> options.placeholder(placeholder)
            is @DrawableRes Int -> options.placeholder(placeholder)
            null -> globalImagePlaceHolder?.run { options.placeholder(this) }
        }
        when (error) {
            is Drawable -> options.error(error)
            is @DrawableRes Int -> options.error(error)
            null -> globalImageError?.run { options.error(this) }
        }
        if (!(res as? String).isNullOrBlank()){
            if (transform != null) {
                options = options.transform(transform)
            } else if (circle) {
                options = options.circleCrop()
            }
            options = options.skipMemoryCache(false)
            if (diskCache) {
                options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            } else {
                options.diskCacheStrategy(DiskCacheStrategy.NONE)
            }
        }
        options
    }, asBitmap, thumbnail)
}

/**
 * 自定义RequestOptions的图片加载
 */
fun ImageView.load(res: Any?, options: () -> RequestOptions?, asBitmap: Boolean = false, thumbnail: Float = 0f): RequestBuilder<*> {
    val url = if (res is String && res.startsWith("//")) "http:$res" else res
    return if (asBitmap) {
        Glide.with(this).asBitmap().load(url)
                .apply(options.invoke() ?: defaultOptions)
                .thumbnail(thumbnail)
    } else {
        Glide.with(this).load(url)
                .apply(options.invoke() ?: defaultOptions)
                .thumbnail(thumbnail)
    }.apply { into(this@load) }
}

/**
 * 代码着色图片
 */
@BindingAdapter("tint")
fun ImageView.tint(tint: Any) {
    when (tint) {
        is Int -> this.imageTintList = ColorStateList.valueOf(tint)
        is String -> this.imageTintList = ColorStateList.valueOf(Color.parseColor(tint))
        else -> throw IllegalStateException("不支持的颜色格式")
    }
}

/**
 * 着色使按钮变灰
 */
fun ImageView.tintDark() {
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val grayColorFilter = ColorMatrixColorFilter(cm)
    this.colorFilter = grayColorFilter
}