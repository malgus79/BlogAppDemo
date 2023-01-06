package com.blogappdemo.domain.camera

import android.graphics.Bitmap
import android.net.Uri
import com.blogappdemo.data.remote.camera.CameraDataSource
import javax.inject.Inject

class CameraRepoImpl @Inject constructor(private val dataSource: CameraDataSource) : CameraRepo {
    override suspend fun uploadPhotoCamera(imageBitmap: Bitmap, description: String) {
        dataSource.uploadPhotoCamera(imageBitmap, description)
    }

    override suspend fun uploadPhotoGallery(imageUri: Uri, description: String) {
        dataSource.uploadPhotoGallery(imageUri, description)
    }

}