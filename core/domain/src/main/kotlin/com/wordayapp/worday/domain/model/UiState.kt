package com.wordayapp.worday.domain.model

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: WordayError? = null,
) {
    val isSuccess: Boolean get() = data != null && error == null && !isLoading
    val isEmpty: Boolean get() = data == null && error == null && !isLoading
}
