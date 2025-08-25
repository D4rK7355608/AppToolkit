package com.d4rk.android.apps.apptoolkit.app.apps.list

import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.AppInfo
import com.d4rk.android.apps.apptoolkit.app.core.utils.dispatchers.StandardDispatcherExtension
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.Error
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TestAppsListViewModel : TestAppsListViewModelBase() {

    companion object {
        @JvmField
        @RegisterExtension
        val dispatcherExtension = StandardDispatcherExtension()
    }

    @Test
    fun `fetch apps - large list`() = runTest(dispatcherExtension.testDispatcher) {
        val apps = (1..10_000).map { AppInfo("App$it", "pkg$it", "url$it") }
        val flow = flow {
            emit(DataState.Loading<List<AppInfo>, Error>())
            emit(DataState.Success<List<AppInfo>, Error>(apps))
        }
        setup(fetchFlow = flow, dispatcher = dispatcherExtension.testDispatcher)
        viewModel.uiState.testSuccess(expectedSize = apps.size)
    }

    @Test
    fun `toggle favorite updates state`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Success<List<AppInfo>, Error>(listOf(AppInfo("App", "pkg", "url"))))
        }
        setup(fetchFlow = flow, dispatcher = dispatcherExtension.testDispatcher)
        toggleAndAssert(packageName = "pkg", expected = true)
        toggleAndAssert(packageName = "pkg", expected = false)
    }

    @Test
    fun `toggle favorite removes preexisting favorite`() = runTest(dispatcherExtension.testDispatcher) {
        val flow = flow {
            emit(DataState.Success<List<AppInfo>, Error>(listOf(AppInfo("App", "pkg", "url"))))
        }
        setup(fetchFlow = flow, initialFavorites = setOf("pkg"), dispatcher = dispatcherExtension.testDispatcher)
        toggleAndAssert(packageName = "pkg", expected = false)
    }
}
