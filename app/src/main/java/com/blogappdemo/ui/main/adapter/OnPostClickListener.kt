package com.blogappdemo.ui.main.adapter

import android.graphics.Bitmap
import com.blogappdemo.data.model.Post

interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
    fun onShareButtonClick(post: Post, shared: Boolean)
    fun onCommentButtonClick(post: Post, commented: Boolean)
    fun onDeleteButtonClick(post: Post)
}