package com.d4rk.android.libs.apptoolkit.core.domain.model.ui


import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenDataStatus
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import kotlinx.coroutines.flow.MutableStateFlow

data class UiStateScreen<T>(
    val screenState : ScreenState = ScreenState.NoData() , var errors : List<UiSnackbar> = emptyList() , val snackbar : UiSnackbar? = null , val data : T? = null
)

data class UiSnackbar(
    var type : String = ScreenMessageType.NONE ,
    val message : UiTextHelper = UiTextHelper.DynamicString(content = "") ,
    val isError : Boolean = true ,
    val timeStamp : Long = 0 ,
)

fun <T> MutableStateFlow<UiStateScreen<T>>.updateData(newDataState : ScreenState , newValues : (T) -> T) {
    value = value.copy(
        screenState = newDataState , data = value.data?.let(newValues)
    )
}

fun <T> MutableStateFlow<UiStateScreen<T>>.updateState(newValues : ScreenState) {
    value = value.copy(screenState = newValues)
}

fun <T> MutableStateFlow<UiStateScreen<T>>.setErrors(errors : List<UiSnackbar>) {
    value = value.copy(errors = errors)
}

fun <T> MutableStateFlow<UiStateScreen<T>>.showSnackbar(snackbar : UiSnackbar) {
    value = value.copy(snackbar = snackbar)
}

fun <T> MutableStateFlow<UiStateScreen<T>>.dismissSnackbar() {
    value = value.copy(snackbar = null)
}

fun <T> MutableStateFlow<UiStateScreen<T>>.setErrors(errors : ArrayList<UiSnackbar>?) {
    this.value = this.value.copy(screenState = ScreenState.Error() , errors = errors ?: ArrayList())
}

fun <T> MutableStateFlow<UiStateScreen<T>>.setLoading() {
    this.value = this.value.copy(screenState = ScreenState.IsLoading())
}

fun <T> MutableStateFlow<UiStateScreen<T>>.getData() : T {
    return value.data ?: throw IllegalStateException("Data is not of expected type.")
}

fun <T> MutableStateFlow<UiStateScreen<T>>.getErrors() : List<UiSnackbar> {
    return value.errors
}

sealed class ScreenState {
    data class NoData(val data : String = ScreenDataStatus.NO_DATA) : ScreenState()
    data class IsLoading(val data : String = ScreenDataStatus.LOADING) : ScreenState()
    data class Success(val data : String = ScreenDataStatus.HAS_DATA) : ScreenState()
    data class Error(val data : String = ScreenDataStatus.ERROR) : ScreenState()
}