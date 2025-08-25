package com.d4rk.android.apps.apptoolkit.app.apps.favorites

import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.AppInfo
import com.d4rk.android.apps.apptoolkit.app.core.utils.dispatchers.StandardDispatcherExtension
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Error
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TestFavoriteAppsViewModel : TestFavoriteAppsViewModelBase() {

    companion object {
        @JvmField
        @RegisterExtension
        val dispatcherExtension = StandardDispatcherExtension()
    }

    @Test
    fun `toggle favorite throws after load`() = runTest(dispatcherExtension.testDispatcher) {
        println("\uD83D\uDE80 [TEST] toggle favorite throws after load")
        val apps = listOf(AppInfo("App", "pkg", "url"))
        val flow = flow {
            emit(DataState.Loading<List<AppInfo>, Error>())
            emit(DataState.Success<List<AppInfo>, Error>(apps))
        }
        setup(
            fetchFlow = flow,
            initialFavorites = emptySet(),
            toggleError = RuntimeException("fail"),
            dispatcher = dispatcherExtension.testDispatcher
        )

        advanceUntilIdle()
        viewModel.toggleFavorite("pkg")
        advanceUntilIdle()
        assertThat(viewModel.favorites.value.contains("pkg")).isFalse()
        println("\uD83C\uDFC1 [TEST DONE] toggle favorite throws after load")
    }
}
