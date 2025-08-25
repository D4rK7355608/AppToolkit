package com.d4rk.android.libs.apptoolkit.app.issuereporter.domain.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build

class DeviceInfo(context: Context) {
    private var versionCode: Int = -1
    private var versionName: String? = null
    private val buildVersion: String = Build.VERSION.INCREMENTAL ?: Build.UNKNOWN
    private val releaseVersion: String = Build.VERSION.RELEASE ?: Build.UNKNOWN
    private val sdkVersion: Int = Build.VERSION.SDK_INT
    private val buildID: String = Build.DISPLAY ?: Build.UNKNOWN
    private val brand: String = Build.BRAND ?: Build.UNKNOWN
    private val manufacturer: String = Build.MANUFACTURER ?: Build.UNKNOWN
    private val device: String = Build.DEVICE ?: Build.UNKNOWN
    private val model: String = Build.MODEL ?: Build.UNKNOWN
    private val product: String = Build.PRODUCT ?: Build.UNKNOWN
    private val hardware: String = Build.HARDWARE ?: Build.UNKNOWN

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private val abis: Array<String> = Build.SUPPORTED_ABIS ?: emptyArray()

    @SuppressLint("NewApi")
    private val abis32Bits: Array<String>? = Build.SUPPORTED_32_BIT_ABIS

    @SuppressLint("NewApi")
    private val abis64Bits: Array<String>? = Build.SUPPORTED_64_BIT_ABIS

    companion object {
        suspend fun create(context: Context): DeviceInfo {
            val info = DeviceInfo(context)
            val packageInfo = runCatching {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }.getOrNull()

            @Suppress("DEPRECATION")
            info.versionCode = packageInfo?.versionCode ?: -1
            info.versionName = packageInfo?.versionName
            return info
        }
    }

    fun toMarkdown(): String {
        return buildString {
            append("Device info:\n")
            append("---\n")
            append("<table>\n")
            append("<tr><td>App version</td><td>$versionName</td></tr>\n")
            append("<tr><td>App version code</td><td>$versionCode</td></tr>\n")
            append("<tr><td>Android build version</td><td>$buildVersion</td></tr>\n")
            append("<tr><td>Android release version</td><td>$releaseVersion</td></tr>\n")
            append("<tr><td>Android SDK version</td><td>$sdkVersion</td></tr>\n")
            append("<tr><td>Android build ID</td><td>$buildID</td></tr>\n")
            append("<tr><td>Device brand</td><td>$brand</td></tr>\n")
            append("<tr><td>Device manufacturer</td><td>$manufacturer</td></tr>\n")
            append("<tr><td>Device name</td><td>$device</td></tr>\n")
            append("<tr><td>Device model</td><td>$model</td></tr>\n")
            append("<tr><td>Device product name</td><td>$product</td></tr>\n")
            append("<tr><td>Device hardware name</td><td>$hardware</td></tr>\n")
            append("<tr><td>ABIs</td><td>${abis.contentToString()}</td></tr>\n")
            append("<tr><td>ABIs (32bit)</td><td>${abis32Bits?.contentToString()}</td></tr>\n")
            append("<tr><td>ABIs (64bit)</td><td>${abis64Bits?.contentToString()}</td></tr>\n")
            append("</table>\n")
        }
    }

    override fun toString(): String {
        return buildString {
            append("App version: $versionName\n")
            append("App version code: $versionCode\n")
            append("Android build version: $buildVersion\n")
            append("Android release version: $releaseVersion\n")
            append("Android SDK version: $sdkVersion\n")
            append("Android build ID: $buildID\n")
            append("Device brand: $brand\n")
            append("Device manufacturer: $manufacturer\n")
            append("Device name: $device\n")
            append("Device model: $model\n")
            append("Device product name: $product\n")
            append("Device hardware name: $hardware\n")
            append("ABIs: ${abis.contentToString()}\n")
            append("ABIs (32bit): ${abis32Bits?.contentToString()}\n")
            append("ABIs (64bit): ${abis64Bits?.contentToString()}")
        }
    }
}
