package com.blogappdemo.data.remote.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeScreenDataSource {

    //datasource va a buscar la info en Firebase
    suspend fun getLatestPosts(): Flow<Result<List<Post>>> = callbackFlow {
        //crearcion de una lista mutable de post
        val postList = mutableListOf<Post>()

        //referencia de post
        var postReference: Query? = null

        //inicializar la referencia
        try {
            postReference = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING)
        } catch (e: Exception) {
            close(e)  //cerrar el canal del callbackFlow
        }

        //suscribir referencia a firebase ->  escuchar todoo el tiempo por datos nuevos en la dase de datos
        val suscription = postReference?.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener  //en caso de != null -> continua al for
            try {
                postList.clear()  //para no duplicar los post, primero se borra la lista
                for (post in value.documents) {
                    //transformar el post.document de firebase al modelo de Post (data)
                    post.toObject(Post::class.java)?.let { fbPost ->

                        //estimar fecha en el caso de que sea nula en el momento que está en el server
                        fbPost.apply {
                            created_at = post.getTimestamp("created_at",
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()
                        }
                        postList.add(fbPost)
                    }
                }
            } catch (e: Exception) {
                close(e)
            }

            //ofrece al flow una lista de <post> cada vez que se pongan en el server
            trySend(Result.Success(postList)).isSuccess
        }
        /* TODO reemplazado al migrar a flow

            withContext(Dispatchers.IO) {

                //peticion a firebase para traer esa peticion de post
                //await: si falla la coroutina devuelve el cancetatioinExceltion
                val querySnapshot = FirebaseFirestore.getInstance().collection("posts")
                    .orderBy("created_at", Query.Direction.ASCENDING).get().await()

                for (post in querySnapshot.documents) {
                    //transformar el post.document de firebase al modelo de Post (data)
                    post.toObject(Post::class.java)?.let { fbPost ->

                        //estimar fecha en el caso de que sea nula en el momento que está en el server
                        fbPost.apply { created_at = post.getTimestamp("created_at", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate() }
                        postList.add(fbPost)
                    }
                }

            }
            return Result.Success(postList)

        */

        //dejar de poner datos en el canal de flow, se cancela la suscripcion, para no obtener mas info sin consumirse
        awaitClose { suscription?.remove() }
    }

}