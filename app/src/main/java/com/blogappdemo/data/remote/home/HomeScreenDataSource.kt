package com.blogappdemo.data.remote.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {
    //datasource va a buscar la info en Firebase
    suspend fun getLatestPosts(): Result<List<Post>> {
        //crearcion de una lista mutable de post
        val postList = mutableListOf<Post>()
        //peticion a firebase para traer esa peticion de post
        //await: si falla la coroutina devuelve el cancetatioinExceltion
        val querySnapshot = FirebaseFirestore.getInstance().collection("post").get().await()
        for (post in querySnapshot.documents) {
            //transformar el post.document de firebase al modelo de Post (data)
            post.toObject(Post::class.java)?.let { fbPost ->
                postList.add(fbPost)
            }
        }
        return Result.Success(postList)
    }
}