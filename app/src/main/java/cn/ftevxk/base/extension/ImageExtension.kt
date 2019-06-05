package cn.ftevxk.base.extension

import android.graphics.Bitmap
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
@BindingAdapter(value = ["bind:res", "bind:tint", "bind:placeholder", "bind:error",
    "bind:transform", "bind:circle", "bind:thumbnail"], requireAll = false)
fun ImageView.load(res: Any?, tint: Int? = null, placeholder: Any? = null, error: Any? = null,
                   transform: Transformation<Bitmap>? = null,
                   circle: Boolean = false, thumbnail: Float = 0f) {
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
    Glide.with(this).load(res).apply(options).thumbnail(thumbnail).into(
            object : CustomViewTarget<ImageView, Drawable>(this) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    this@load.setImageDrawable(errorDrawable)
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    this@load.setImageDrawable(placeholder)
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    val drawable = resource.mutate()
                    tint?.run { DrawableCompat.setTint(drawable, this) }
                    this@load.setImageDrawable(drawable)
                }
            })
}