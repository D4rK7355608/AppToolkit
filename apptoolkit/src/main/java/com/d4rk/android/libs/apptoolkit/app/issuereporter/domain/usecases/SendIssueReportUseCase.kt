package com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.usecases

import com.d4rk.android.libs.apptoolkit.app.issuereporter.data.IssueReporterRepository
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.IssueReportResult
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.Report
import com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model.github.GithubTarget
import com.d4rk.android.libs.apptoolkit.core.domain.usecases.Repository

class SendIssueReportUseCase(
    private val repository: IssueReporterRepository
) : Repository<SendIssueReportUseCase.Params, Result<IssueReportResult>> {

    data class Params(
        val report: Report,
        val target: GithubTarget,
        val token: String?
    )

    override suspend fun invoke(param: Params): Result<IssueReportResult> =
        runCatching {
            repository.sendReport(param.report, param.target, param.token)
        }
}
