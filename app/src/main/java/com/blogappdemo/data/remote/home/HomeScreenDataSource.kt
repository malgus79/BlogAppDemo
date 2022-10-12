package com.blogappdemo.data.remote.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {
    //datasource va a buscar la info en Firebase
    suspend fun getLatestPosts(): Result<List<Post>> {
        //crearcion de una lista mutable de post
        val postList = mutableListOf<Post>()
        //peticion a firebase para traer esa peticion de post
        //await: si falla la coroutina devuelve el cancetatioinExceltion
        val querySnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
        for (post in querySnapshot.documents) {
            //transformar el post.document de firebase al modelo de Post (data)
            post.toObject(Post::class.java)?.let { fbPost ->

                //estimar fecha en el caso de que sea nula en el momento que está en el server
                fbPost.apply {
                    created_at = post.getTimestamp("created_at", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate() }
                    postList.add(fbPost)
            }
        }
        return Result.Success(postList)
    }
}