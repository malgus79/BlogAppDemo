package com.blogappdemo.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(
    @Exclude @JvmField  //para excluir de firebase
    val id: String = "",
    @ServerTimestamp  //transformar la info que viene de firebase que lo mapea a date
    var created_at: Date? = null,
    val post_image: String = "",
    val post_description: String = "",
    val poster: Poster? = null,
    val likes: Long = 0,
    @Exclude @JvmField //para excluir de firebase
    var liked: Boolean = false,
)

data class Poster(
    val username: String? = "",
    val uid: String? = null,
    val profile_picture: String = "",
)