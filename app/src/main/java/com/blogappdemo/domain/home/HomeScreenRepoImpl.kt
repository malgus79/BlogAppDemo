package com.blogappdemo.domain.home

import android.widget.LinearLayout
import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.blogappdemo.data.remote.home.HomeScreenDataSource
import javax.inject.Inject

class HomeScreenRepoImpl @Inject constructor(private val dataSource: HomeScreenDataSource) :
    HomeScreenRepo {

    //el repo va hacia el datasource a buscar la info
    override suspend fun getLatestPosts(): Result<List<Post>> = dataSource.getLatestPosts()

    override suspend fun registerLikeButtonState(postId: String, liked: Boolean) =
        dataSource.registerLikeButtonState(postId, liked)

    override suspend fun registerShareButtonState(postId: String, shared: Boolean) =
        dataSource.registerShareButtonState(postId, shared)

    override suspend fun registerCommentButtonState(postId: String, commented: Boolean) =
        dataSource.registerCommentButtonState(postId, commented)

    override suspend fun deleteButtonState(postId: String) {
        dataSource.deleteButtonState(postId)
    }
}