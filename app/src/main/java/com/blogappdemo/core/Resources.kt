package com.blogappdemo.core

sealed class Resources<out T> {
    //sin constuctor, no recibe nada
    class Loading<out T> : Resources<T>()
    //si va a recibir data -> data class
    data class Success<out T>(val data: T) : Resources<T>()
    data class Failure(val exception: Exception) : Resources<Nothing>()
}