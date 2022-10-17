package com.blogappdemo.presentation.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import com.blogappdemo.core.Result
import com.blogappdemo.domain.camera.CameraRepo

class CameraViewModel(private val repo: CameraRepo) : ViewModel() {

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

class CameraViewModelFactory(private val repo: CameraRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CameraRepo::class.java).newInstance(repo)
    }
}