package com.blogappdemo.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import com.blogappdemo.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AuthDataSource {

    //login
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            val authResult =
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            authResult.user
        }
    }

    //register
    suspend fun signUp(email: String, password: String, username: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            val authResult =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            authResult.user?.uid?.let { uid ->
                FirebaseFirestore.getInstance().collection("users").document(uid).set(
                    User(email, username)).await()
            }
            authResult.user
        }
    }

    //actualizar perfil de usuario
    suspend fun updateUserProfile(imageBitmap: Bitmap, username: String) {
        //credencial de usuario
        val user = FirebaseAuth.getInstance().currentUser
        //referencia de foto del usuario
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        //obtener la uri de la foto
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var downloadUrl =
            imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        //setear la foto al profile
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .setPhotoUri(Uri.parse(downloadUrl))
            .build()
        user?.updateProfile(profileUpdates)?.await()
    }
}