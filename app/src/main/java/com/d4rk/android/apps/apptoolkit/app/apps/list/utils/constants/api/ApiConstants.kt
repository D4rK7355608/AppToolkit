package com.d4rk.android.apps.apptoolkit.app.apps.list.utils.constants.api

object ApiHost {
    const val GITHUB_RAW_URL: String = "https://raw.githubusercontent.com"
    const val USERNAME: String = "D4rK7355608"
    const val API_REPO: String = "com.d4rk.apis"
    const val API_BRANCH: String = "main"
    const val API_FOLDER_PATH: String = "App%20Toolkit"
}

object ApiEnvironments {
    const val ENV_DEBUG: String = "debug"
    const val ENV_RELEASE: String = "release"
}

object ApiPaths {
    const val DEVELOPER_APPS_API: String = "/en/home/api_android_apps.json"
}

object ApiConstants {
    const val BASE_REPOSITORY_URL: String = "${ApiHost.GITHUB_RAW_URL}/${ApiHost.USERNAME}/${ApiHost.API_REPO}/refs/heads/${ApiHost.API_BRANCH}/${ApiHost.API_FOLDER_PATH}"
}