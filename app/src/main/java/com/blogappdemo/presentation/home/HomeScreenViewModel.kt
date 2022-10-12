package com.blogappdemo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.blogappdemo.core.Result
import com.blogappdemo.domain.home.HomeScreenRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

class HomeScreenViewModel(private val repo: HomeScreenRepo): ViewModel() {

    //solicitar info al repo en un hilo secundario (Dispacher.IO)
    fun fetchLatestPosts() = liveData(Dispatchers.IO) {
        //emitir valor de carga
        emit(Result.Loading())
        /* TODO reemplazado al migrar a flow
        try {
            emit(repo.getLatestPosts())
        }catch (e:Exception) {
            emit(Result.Failure(e))
        }
         */

        kotlin.runCatching {
            //operacion a ejecutar en el servidor
            repo.getLatestPosts()
        }.onSuccess { flowList ->
            flowList.collectLatest { emit(it) }
        }.onFailure { throwable ->
            emit(Result.Failure(Exception(throwable.message)))
        }
    }
}

//se crea el Factory para poder generar una instancia del vewModel con un parametro en el constructor
class HomeScreenViewModelFactory(private val repo: HomeScreenRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo)
    }

}