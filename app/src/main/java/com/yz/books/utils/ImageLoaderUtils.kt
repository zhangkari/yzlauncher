package com.yz.books.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.yz.books.R
import com.yz.books.common.glide.GlideApp

/**
 * @author lilin
 * @time on 2019-12-29 16:05
 */
object ImageLoaderUtils {

    fun withLogo(url: String,
                 imageView: ImageView) {

        GlideApp.with(imageView.context)
            .load(url)
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.img_default_logo)
            .error(R.drawable.img_default_logo)
            .into(imageView)
    }

    fun withBookCover(url: String,
                      imageView: ImageView,
                      scaleType: ImageView.ScaleType? = null) {
        imageView.scaleType = scaleType ?: ImageView.ScaleType.CENTER_CROP

        GlideApp.with(imageView.context)
            .asBitmap()
            .load(url)
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            //.placeholder(R.mipmap.ic_photo)
            //.error(R.mipmap.ic_photo)
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.resources, resource)
                    circularBitmapDrawable.cornerRadius = 10f
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

    fun withUserHead(url: String,
                      imageView: ImageView) {
        GlideApp.with(imageView.context)
            .asBitmap()
            .load(url)
            //.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            //.placeholder(R.mipmap.ic_photo)
            //.error(R.mipmap.ic_photo)
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.resources, resource)
                    circularBitmapDrawable.cornerRadius = 20f
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }
}