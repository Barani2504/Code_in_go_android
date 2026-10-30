package com.simats.codeingo.data.network.model

/**
 * Sealed result wrapper for network requests, providing typed success and failure cases
 * with human-readable error messages for UI presentation.
 */
sealed class ApiResult<out T> {

    data class Success<out T>(val data: T) : ApiResult<T>()

    data class HttpError(
        val code: Int,
        val message: String,
        val errorBody: String? = null
    ) : ApiResult<Nothing>()

    data class NetworkError(
        val exception: Throwable,
        val message: String = "No network connection. Operating in offline mode."
    ) : ApiResult<Nothing>()

    data class Unauthorized(
        val message: String = "Session expired or credentials invalid. Please sign in again."
    ) : ApiResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = !isSuccess

    fun getOrNull(): T? = (this as? Success)?.data

    fun errorMessage(): String = when (this) {
        is Success -> ""
        is HttpError -> when (code) {
            400 -> message.ifEmpty { "Invalid request. Please check your input." }
            403 -> "Access denied to this resource."
            404 -> "Requested resource not found on server."
            409 -> message.ifEmpty { "An account with this email already exists." }
            429 -> "Too many requests. Please wait a moment and try again."
            in 500..599 -> "Server encountered an error ($code). Local progress remains saved."
            else -> message.ifEmpty { "Server returned HTTP error $code" }
        }
        is NetworkError -> message
        is Unauthorized -> message
    }
}
