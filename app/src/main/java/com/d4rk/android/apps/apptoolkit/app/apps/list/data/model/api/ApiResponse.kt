package com.d4rk.android.apps.apptoolkit.app.apps.list.data.model.api

import com.d4rk.android.apps.apptoolkit.app.apps.list.domain.model.AppInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("data") val data: AppDataWrapper
)

@Serializable
data class AppDataWrapper(
    @SerialName("apps") val apps: List<AppInfo>
)