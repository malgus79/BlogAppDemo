package com.blogappdemo.ui.main.adapter

import com.blogappdemo.data.model.Post

interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
}