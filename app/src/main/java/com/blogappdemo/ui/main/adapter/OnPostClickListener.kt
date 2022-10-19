package com.blogappdemo.ui.main.adapter

import com.blogappdemo.data.model.Post

interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
    fun onShareButtonClick(post: Post, shared: Boolean)
    fun onCommentButtonClick(post: Post, commented: Boolean)
}