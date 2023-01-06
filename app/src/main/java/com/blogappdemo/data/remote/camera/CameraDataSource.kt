package com.blogappdemo.data.remote.camera

import android.graphics.Bitmap
import android.net.Uri
import com.blogappdemo.data.model.Post
import com.blogappdemo.data.model.Poster
import com.blogappdemo.utils.Constants.POSTS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class CameraDataSource @Inject constructor() {

    suspend fun uploadPhotoCamera(imageBitmap: Bitmap, description: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val randomName = UUID.randomUUID().toString()
        val imageRef =
            FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val downloadUrl =
            imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()

        user?.let {
            it.displayName?.let { displayName ->
                FirebaseFirestore.getInstance().collection(POSTS).add(Post(
                    poster = Poster(username = displayName,
                        uid = user.uid,
                        profile_picture = it.photoUrl.toString()),
                    post_image = downloadUrl,
                    post_description = description,
                    likes = 0))
            }
        }
    }

    suspend fun uploadPhotoGallery(imageUri: Uri, description: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val randomName = UUID.randomUUID().toString()
        val imageRef =
            FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomName")
        imageUri.toString()
        val downloadUrl = imageRef.putFile(imageUri).await().storage.downloadUrl.await().toString()

        user?.let {
            it.displayName?.let { displayName ->
                FirebaseFirestore.getInstance().collection(POSTS).add(Post(
                    poster = Poster(username = displayName,
                        uid = user.uid,
                        profile_picture = it.photoUrl.toString()),
                    post_image = downloadUrl,
                    post_description = description,
                    likes = 0))
            }
        }
    }
}