package com.d4rk.android.libs.apptoolkit.app.issuereporter.ui

import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.GithubTarget
import com.d4rk.android.libs.apptoolkit.core.utils.dispatchers.TestDispatchers
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
open class TestIssueReporterViewModelBase {

    protected lateinit var dispatcherProvider: TestDispatchers
    protected lateinit var viewModel: IssueReporterViewModel

    protected fun setup(
        engine: MockEngine,
        githubTarget: GithubTarget = GithubTarget("user", "repo"),
        githubToken: String = "",
        testDispatcher: TestDispatcher
    ) {
        println("\uD83E\uDDEA [SETUP] githubTarget=$githubTarget tokenProvided=${githubToken.isNotBlank()}")
        dispatcherProvider = TestDispatchers(testDispatcher)
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json()
            }
        }
        viewModel = IssueReporterViewModel(dispatcherProvider, client, githubTarget, githubToken)
        println("\u2705 [SETUP] ViewModel initialized")
    }
}

