package com.blogappdemo.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.blogappdemo.core.Result
import com.blogappdemo.domain.auth.LoginRepo
import kotlinx.coroutines.Dispatchers

class LoginScreenViewModel(private val repo: LoginRepo) : ViewModel() {

    //metodo de logeo
    fun signIn(email: String, password: String) = liveData( Dispatchers.Main) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.signIn(email, password)))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}

class LoginScreenViewModelFactory(private val repo: LoginRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginScreenViewModel(repo) as T
    }
}