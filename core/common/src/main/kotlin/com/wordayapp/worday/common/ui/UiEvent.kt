package com.wordayapp.worday.common.ui

/**
 * Tek seferlik UI olayları için (snackbar, navigation, toast).
 * ViewModel'dan SharedFlow ile emit edilir, Compose'da collectAsEffect ile toplanır.
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    object NavigateBack : UiEvent()
    data class ShowToast(val message: String) : UiEvent()
}