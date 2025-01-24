package com.d4rk.android.libs.apptoolkit.utils.interfaces.providers

interface PrivacySettingsProvider {
    val privacyPolicyUrl: String
        get() = "https://default.privacy.policy.url"

    val termsOfServiceUrl: String
        get() = "https://default.terms.of.service.url"

    val codeOfConductUrl: String
        get() = "https://default.code.of.conduct.url"

    val legalNoticesUrl: String
        get() = "https://default.legal.notices.url"

    val licenseUrl: String
        get() = "https://www.gnu.org/licenses/gpl-3.0"

    fun openPermissionsScreen()
    fun openAdsScreen()
    fun openUsageAndDiagnosticsScreen()
}