package com.blogappdemo.domain.camera

import android.graphics.Bitmap
import android.net.Uri

interface CameraRepo {
    suspend fun uploadPhotoCamera(imageBitmap: Bitmap, description: String)
    suspend fun uploadPhotoGallery(imageUri: Uri, description: String)
}