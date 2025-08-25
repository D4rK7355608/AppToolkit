package com.d4rk.android.libs.apptoolkit.app.issuereporter.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.actions.IssueReporterAction
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.actions.IssueReporterEvent
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.DeviceInfo
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.IssueReportResult
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.Report
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.ExtraInfo
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.GithubTarget
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.ui.UiIssueReporterScreen
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.usecases.SendIssueReportUseCase
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.dismissSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.showSnackbar
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateData
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.updateState
import com.d4rk.android.libs.apptoolkit.core.ui.base.ScreenViewModel
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.ScreenMessageType
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.UiTextHelper
import kotlinx.coroutines.flow.update
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch

class IssueReporterViewModel(
    private val sendIssueReport: SendIssueReportUseCase,
    private val githubTarget: GithubTarget,
    private val githubToken: String,
) : ScreenViewModel<UiIssueReporterScreen, IssueReporterEvent, IssueReporterAction>(
    initialState = UiStateScreen(
        screenState = ScreenState.Success(),
        data = UiIssueReporterScreen()
    )
) {
    override fun onEvent(event: IssueReporterEvent) {
        when (event) {
            is IssueReporterEvent.UpdateTitle -> updateTitle(event.value)
            is IssueReporterEvent.UpdateDescription -> updateDescription(event.value)
            is IssueReporterEvent.UpdateEmail -> updateEmail(event.value)
            is IssueReporterEvent.SetAnonymous -> setAnonymous(event.anonymous)
            is IssueReporterEvent.Send -> sendReport(event.context)
            IssueReporterEvent.DismissSnackbar -> screenState.dismissSnackbar()
        }
    }

    private fun updateTitle(value: String) {
        viewModelScope.launch {
            screenState.updateData(newState = screenState.value.screenState) { current ->
                current.copy(title = value)
            }
        }
    }

    private fun updateDescription(value: String) {
        viewModelScope.launch {
            screenState.updateData(newState = screenState.value.screenState) { current ->
                current.copy(description = value)
            }
        }
    }

    private fun updateEmail(value: String) {
        viewModelScope.launch {
            screenState.updateData(newState = screenState.value.screenState) { current ->
                current.copy(email = value)
            }
        }
    }

    private fun setAnonymous(value: Boolean) {
        viewModelScope.launch {
            screenState.updateData(newState = screenState.value.screenState) { current ->
                current.copy(anonymous = value)
            }
        }
    }

    private fun sendReport(context: Context) {
        viewModelScope.launch {
            val data = screenState.value.data ?: return@launch

            if (data.title.isBlank() || data.description.isBlank()) {
                screenState.showSnackbar(
                    snackbar = UiSnackbar(
                        message = UiTextHelper.StringResource(R.string.error_invalid_report),
                        timeStamp = System.currentTimeMillis(),
                        isError = true,
                        type = ScreenMessageType.SNACKBAR
                    )
                )
                return@launch
            }

            screenState.updateState(ScreenState.IsLoading())
            val deviceInfo = DeviceInfo.create(context)
            val extraInfo = ExtraInfo()
            val report = Report(
                title = data.title,
                description = data.description,
                deviceInfo = deviceInfo,
                extraInfo = extraInfo,
                email = data.email.ifBlank { null }
            )

            val result = sendIssueReport(
                SendIssueReportUseCase.Params(
                    report = report,
                    target = githubTarget,
                    token = githubToken.takeIf { it.isNotBlank() }
                )
            )

            result.fold(
                onSuccess = { outcome ->
                    when (outcome) {
                        is IssueReportResult.Success -> {
                            screenState.update { current ->
                                val updated = current.data?.copy(issueUrl = outcome.url)
                                current.copy(
                                    screenState = ScreenState.Success(),
                                    data = updated,
                                    snackbar = UiSnackbar(
                                        message = UiTextHelper.StringResource(R.string.snack_report_success),
                                        isError = false,
                                        timeStamp = System.currentTimeMillis(),
                                        type = ScreenMessageType.SNACKBAR,
                                    )
                                )
                            }
                        }

                        is IssueReportResult.Error -> {
                            val msg = when (outcome.status) {
                                HttpStatusCode.Unauthorized -> UiTextHelper.StringResource(R.string.error_unauthorized)
                
                                HttpStatusCode.Forbidden -> UiTextHelper.StringResource(R.string.error_forbidden)
                                HttpStatusCode.Gone -> UiTextHelper.StringResource(R.string.error_gone)
                                HttpStatusCode.UnprocessableEntity -> UiTextHelper.StringResource(R.string.error_unprocessable)
                                else -> UiTextHelper.StringResource(R.string.snack_report_failed)
                            }
                            screenState.update { current ->
                                current.copy(
                                    screenState = ScreenState.Error(),
                                    snackbar = UiSnackbar(
                                        message = msg,
                                        isError = true,
                                        timeStamp = System.currentTimeMillis(),
                                        type = ScreenMessageType.SNACKBAR,
                                    )
                                )
                            }
                        }
                    }
                },
                onFailure = {
                    screenState.update { current ->
                        current.copy(
                            screenState = ScreenState.Error(),
                            snackbar = UiSnackbar(
                                message = UiTextHelper.StringResource(R.string.snack_report_failed),
                                isError = true,
                                timeStamp = System.currentTimeMillis(),
                                type = ScreenMessageType.SNACKBAR,
                            )
                        )
                    }
                }
            )
        }
    }
}