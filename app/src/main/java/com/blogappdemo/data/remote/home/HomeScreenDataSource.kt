package com.blogappdemo.data.remote.home

import com.blogappdemo.core.Result
import com.blogappdemo.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeScreenDataSource {

    //datasource va a buscar la info en Firebase
    suspend fun getLatestPosts(): Result<List<Post>> {
        //crearcion de una lista mutable de post
        val postList = mutableListOf<Post>()

        //especificar el dispacher
        withContext(Dispatchers.IO) {

            //peticion a firebase para traer esa peticion de post
            //await: si falla la coroutina devuelve el cancetatioinExceltion
            val querySnapshot = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("created_at", Query.Direction.DESCENDING).get().await()

            for (post in querySnapshot.documents) {
                //transformar el post.document de firebase al modelo de Post (data)
                post.toObject(Post::class.java)?.let { fbPost ->

                    //ejecutar metodo de verificacion del likes
                    val isLiked = FirebaseAuth.getInstance().currentUser?.let { safeUser ->
                        isPostLiked(post.id, safeUser.uid)
                    }

                    //estimar fecha en el caso de que sea nula en el momento que está en el server
                    fbPost.apply {
                        created_at = post.getTimestamp("created_at",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()

                        //poner al post el valor (true/false) si esta o no likeado
                        id = post.id
                        if (isLiked != null) {
                            liked = isLiked
                        }
                    }
                    postList.add(fbPost)
                }
            }
        }
        return Result.Success(postList)
    }

    //verificar si el array contiene el usuario (si esta likeado el post)
    private suspend fun isPostLiked(postId: String, uid: String): Boolean {
        val post =
            FirebaseFirestore.getInstance().collection("postsLikes").document(postId).get().await()
        if (!post.exists()) return false
        val likeArray: List<String> = post.get("likes") as List<String>
        return likeArray.contains(uid)
    }

    fun registerLikeButtonState(postId: String, liked: Boolean) {

        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        //obtener datos de firebase, del post que se está linkeando
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        val postsLikesRef =
            FirebaseFirestore.getInstance().collection("postsLikes").document(postId)

        //instamcia de la base de datos
        val database = FirebaseFirestore.getInstance()

        //correr transaccion sobre la base de datos
        database.runTransaction { transaction ->
            //obtener el post
            val snapshot = transaction.get(postRef)
            //ver la cantidad de likes que tiene
            val likeCount = snapshot.getLong("likes")

            if (likeCount != null) {
                if (likeCount >= 0) {
                    if (liked) {
                        //si el post existe
                        if (transaction.get(postsLikesRef).exists()) {
                            //adjuntar el usuario
                            transaction.update(postsLikesRef, "likes", FieldValue.arrayUnion(uid))
                        } else {
                            //si es el primer like
                            transaction.set(
                                postsLikesRef,
                                hashMapOf("likes" to arrayListOf(uid)),
                                SetOptions.merge()
                            )
                        }

                        //incrementar en 1 el numero de likes dle post
                        transaction.update(postRef, "likes", increment)
                    } else {
                        //disminuir en 1 los likes y remover el usuario del arrays
                        transaction.update(postRef, "likes", decrement)
                        transaction.update(postsLikesRef, "likes", FieldValue.arrayRemove(uid))
                    }
                }
            }
        }.addOnFailureListener {
            throw Exception(it.message)
        }
    }
}