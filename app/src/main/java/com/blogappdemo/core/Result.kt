package com.blogappdemo.core

sealed class Result<out T> {
    //sin constuctor, no recibe nada
    class Loading<out T> : Result<T>()
    //si va a recibir data -> data class
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}