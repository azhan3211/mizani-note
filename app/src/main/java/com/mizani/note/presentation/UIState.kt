package com.mizani.note.presentation

sealed class UIState<out T: Any, out E: Throwable> {

    object Loading: UIState<Nothing, Nothing>()
    data class Success<T: Any>(val data: T): UIState<T, Nothing>()
    data class Error<E: Throwable>(val error: Throwable): UIState<Nothing, E>()

}