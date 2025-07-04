package com.d4rk.android.apps.apptoolkit.app.apps.favorites

import app.cash.turbine.test
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.ui.FavoriteAppsViewModel
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.AppInfo
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.ui.UiHomeScreen
import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.usecases.FetchDeveloperAppsUseCase
import com.d4rk.android.apps.apptoolkit.app.core.TestDispatchers
import com.d4rk.android.apps.apptoolkit.core.data.datastore.DataStore
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.DataState
import com.d4rk.android.libs.apptoolkit.core.domain.model.network.RootError
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.ScreenState
import com.d4rk.android.libs.apptoolkit.core.domain.model.ui.UiStateScreen
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import org.junit.jupiter.api.Assertions.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
open class TestFavoriteAppsViewModelBase {

    protected lateinit var dispatcherProvider: TestDispatchers
    protected lateinit var viewModel: FavoriteAppsViewModel
    private lateinit var fetchUseCase: FetchDeveloperAppsUseCase
    private lateinit var dataStore: DataStore

    protected fun setup(
        fetchFlow: Flow<DataState<List<AppInfo>, RootError>>,
        initialFavorites: Set<String> = emptySet(),
        testDispatcher: TestDispatcher,
        favoritesFlow: Flow<Set<String>>? = null,
        toggleError: Throwable? = null
    ) {
        dispatcherProvider = TestDispatchers(testDispatcher)
        fetchUseCase = mockk()
        dataStore = mockk(relaxed = true)

        val favFlow = favoritesFlow ?: MutableStateFlow(initialFavorites)
        every { dataStore.favoriteApps } returns favFlow

        if (toggleError != null) {
            coEvery { dataStore.toggleFavoriteApp(any()) } throws toggleError
        } else if (favFlow is MutableStateFlow<Set<String>> || favFlow is MutableSharedFlow<Set<String>>) {
            coEvery { dataStore.toggleFavoriteApp(any()) } coAnswers {
                val pkg = it.invocation.args[0] as String
                val current = when (favFlow) {
                    is MutableStateFlow -> favFlow.value
                    is MutableSharedFlow -> favFlow.replayCache.lastOrNull() ?: emptySet()
                    else -> emptySet()
                }.toMutableSet()
                if (!current.add(pkg)) current.remove(pkg)
                when (favFlow) {
                    is MutableStateFlow -> favFlow.value = current
                    is MutableSharedFlow -> favFlow.emit(current)
                }
            }
        } else {
            coEvery { dataStore.toggleFavoriteApp(any()) } returns Unit
        }

        coEvery { fetchUseCase.invoke() } returns fetchFlow

        viewModel = FavoriteAppsViewModel(fetchUseCase, dataStore, dispatcherProvider)
    }

    protected suspend fun Flow<UiStateScreen<UiHomeScreen>>.testSuccess(
        expectedSize: Int,
        testDispatcher: TestDispatcher
    ) {
        this@testSuccess.test {
            val first = awaitItem()
            assertTrue(first.screenState is ScreenState.IsLoading)
            testDispatcher.scheduler.advanceUntilIdle()

            val second = awaitItem()
            assertTrue(second.screenState is ScreenState.Success)
            assertThat(second.data?.apps?.size).isEqualTo(expectedSize)
            cancelAndIgnoreRemainingEvents()
        }
    }

    protected suspend fun Flow<UiStateScreen<UiHomeScreen>>.testEmpty(testDispatcher: TestDispatcher) {
        this@testEmpty.test {
            val first = awaitItem()
            assertTrue(first.screenState is ScreenState.IsLoading)
            testDispatcher.scheduler.advanceUntilIdle()

            val second = awaitItem()
            assertTrue(second.screenState is ScreenState.NoData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    protected suspend fun Flow<UiStateScreen<UiHomeScreen>>.testError(testDispatcher: TestDispatcher) {
        this@testError.test {
            val first = awaitItem()
            assertTrue(first.screenState is ScreenState.IsLoading)
            testDispatcher.scheduler.advanceUntilIdle()
            expectNoEvents()
            // Error flow doesn't update state, so it should remain loading
            val current = viewModel.uiState.value
            assertTrue(current.screenState is ScreenState.IsLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    protected fun toggleAndAssert(packageName: String, expected: Boolean, testDispatcher: TestDispatcher) {
        viewModel.toggleFavorite(packageName)
        testDispatcher.scheduler.advanceUntilIdle()
        val favorites = viewModel.favorites.value
        assertThat(favorites.contains(packageName)).isEqualTo(expected)
    }
}
