package com.blogappdemo.domain.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post

interface HomeScreenRepo {

    //metodo para ir a buscar la info al servidor
    suspend fun getLatestPosts(): Result<List<Post>>
}