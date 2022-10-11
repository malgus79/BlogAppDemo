package com.blogappdemo.domain.home

import com.blogappdemo.core.Resources
import com.blogappdemo.data.model.Post
import com.blogappdemo.data.remote.home.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource): HomeScreenRepo {

    //el repo va hacia el datasource a buscar la info
    override suspend fun getLatestPosts(): Resources<List<Post>> = dataSource.getLatestPosts()
}