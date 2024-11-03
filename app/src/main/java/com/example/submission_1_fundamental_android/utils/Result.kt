package com.example.submission_1_fundamental_android.utils

sealed class Result<out T> private constructor(){
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val ignoredException: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}