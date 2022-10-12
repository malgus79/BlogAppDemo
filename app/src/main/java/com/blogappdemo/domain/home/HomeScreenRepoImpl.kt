package com.blogappdemo.domain.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.blogappdemo.data.remote.home.HomeScreenDataSource
import kotlinx.coroutines.flow.Flow

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource): HomeScreenRepo {

    //el repo va hacia el datasource a buscar la info
    override suspend fun getLatestPosts(): Flow<Result<List<Post>>> = dataSource.getLatestPosts()
}