package com.blogappdemo.domain

import com.blogappdemo.core.Resources
import com.blogappdemo.data.model.Post

interface HomeScreenRepo {

    //metodo para ir a buscar la info al servidor
    suspend fun getLatestPosts(): Resources<List<Post>>
}