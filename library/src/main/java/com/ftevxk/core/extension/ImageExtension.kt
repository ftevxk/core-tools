package com.ftevxk.core.extension

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition


/**
 * 图片加载
 * placeholder和error仅支持Drawable或@DrawableRes
 * transform和circle仅有一个，优先transform
 * @param res 任意图片资源
 */
@BindingAdapter(value = ["res", "tint", "placeholder", "error",
    "asBitmap", "transform", "circle", "thumbnail"], requireAll = false)
fun ImageView.load(res: Any?, tint: Int? = null, placeholder: Any? = null, error: Any? = null,
                   asBitmap: Boolean = false, transform: Transformation<Bitmap>? = null,
                   circle: Boolean = false, thumbnail: Float = 0f) {
    this.load(res, {
        var options = RequestOptions()
        when (placeholder) {
            is @DrawableRes Int -> options.placeholder(placeholder)
            is Drawable -> options.placeholder(placeholder)
        }
        when (error) {
            is @DrawableRes Int -> options.error(error)
            is Drawable -> options.error(error)
        }
        if (transform != null) {
            options = options.transform(transform)
        } else if (circle) {
            options = options.circleCrop()
        }
        options
    }, tint, asBitmap, thumbnail)
}

/**
 * 自定义RequestOptions的图片加载
 */
fun ImageView.load(res: Any?, options: () -> RequestOptions?, tint: Int? = null, asBitmap: Boolean = false, thumbnail: Float = 0f) {
    if (asBitmap) {
        Glide.with(this).asBitmap().load(res).apply(options.invoke() ?: RequestOptions())
                .thumbnail(thumbnail).into(object : CustomViewTarget<ImageView, Bitmap>(this) {

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        this@load.setImageDrawable(errorDrawable)
                    }

                    override fun onResourceCleared(placeholder: Drawable?) {
                        this@load.setImageDrawable(placeholder)
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val drawable = BitmapDrawable(this@load.resources, resource)
                        tint?.run { DrawableCompat.setTint(drawable, this) }
                        this@load.setImageDrawable(drawable)
                    }
                })
    } else {
        if (tint != null) {
            Glide.with(this).load(res).apply(options.invoke() ?: RequestOptions())
                    .thumbnail(thumbnail).into(object : CustomViewTarget<ImageView, Drawable>(this) {

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            this@load.setImageDrawable(errorDrawable)
                        }

                        override fun onResourceCleared(placeholder: Drawable?) {
                            this@load.setImageDrawable(placeholder)
                        }

                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            val drawable = resource.mutate()
                            tint.run { DrawableCompat.setTint(drawable, this) }
                            this@load.setImageDrawable(drawable)
                        }
                    })
        } else {
            Glide.with(this).load(res).apply(options.invoke() ?: RequestOptions())
                    .thumbnail(thumbnail).into(this)
        }
    }
}