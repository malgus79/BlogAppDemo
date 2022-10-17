package com.blogappdemo.presentation.auth

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.blogappdemo.core.Result
import com.blogappdemo.domain.auth.AuthRepo
import kotlinx.coroutines.Dispatchers

class AuthViewModel(private val repo: AuthRepo) : ViewModel() {

    //metodo de logeo
    fun signIn(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.signIn(email, password)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    //metodo para registrar usuario
    fun signUp(email: String, password: String, username: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.signUp(email, password, username)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    //actualizar perfil de usuario: imagen y nombre
    fun updateUserProfile(imageBitmap: Bitmap, username: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.updateProfile(imageBitmap, username)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    //actualizar perfil de usuario: solo imagen
    fun updateUserProfileImage(imageBitmap: Bitmap) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.updateProfileImage(imageBitmap)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

class AuthViewModelFactory(private val repo: AuthRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AuthRepo::class.java).newInstance(repo)
    }
}