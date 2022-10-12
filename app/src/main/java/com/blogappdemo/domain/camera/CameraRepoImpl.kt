package com.blogappdemo.domain.camera

import android.graphics.Bitmap

class CameraRepoImpl: CameraRepo {
    override suspend fun uploadPhoto(imageBitmap: Bitmap, description: String) {
        dataSource.uploadPhoto(imageBitmap, description)
    }

}