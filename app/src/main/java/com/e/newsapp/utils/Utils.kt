package com.e.newsapp.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun ImageView.loadImage(url: String?) {
    url?.let {
        Glide.with(this)
            .load(it)
            .into(this)
    }
}