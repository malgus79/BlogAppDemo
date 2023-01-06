package com.blogappdemo.presentation.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.blogappdemo.core.Result
import com.blogappdemo.domain.camera.CameraRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repo: CameraRepoImpl) : ViewModel() {

    fun uploadPhotoCamera(imageBitmap: Bitmap, description: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.uploadPhotoCamera(imageBitmap, description)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun uploadPhotoGallery(imageUri: Uri, description: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.uploadPhotoGallery(imageUri, description)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}