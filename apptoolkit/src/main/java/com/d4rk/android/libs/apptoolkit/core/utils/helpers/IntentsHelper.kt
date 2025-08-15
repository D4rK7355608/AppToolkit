package com.d4rk.android.libs.apptoolkit.core.utils.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.utils.constants.links.AppLinks
import java.net.URLEncoder

/**
 * A utility object for performing common operations such as opening URLs, activities, and app notification settings.
 *
 * This object provides functions to open a URL in the default browser, open an activity, and open the app's notification settings.
 * All operations are performed in the context of an Android application.
 */
object IntentsHelper {

    /**
     * Opens a specified URL in the default browser.
     *
     * This function creates an Intent with the ACTION_VIEW action and the specified URL, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the URL should be opened.
     * @param url The URL to open.
     */
    fun openUrl(context : Context , url : String) {
        Intent(Intent.ACTION_VIEW , url.toUri()).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens a specified activity.
     *
     * This function creates an Intent with the specified activity class, and starts an activity with this intent. The activity runs in a new task.
     *
     * @param context The Android context in which the activity should be opened.
     * @param activityClass The class of the activity to open.
     */
    fun openActivity(context : Context , activityClass : Class<*>) {
        Intent(context , activityClass).let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Opens the app's notification settings.
     *
     * This function creates an Intent with the ACTION_APP_NOTIFICATION_SETTINGS action and the app's package name, and starts an activity with this intent.
     * The activity runs in a new task.
     *
     * @param context The Android context in which the app's notification settings should be opened.
     */
    fun openAppNotificationSettings(context : Context) {
        val intent : Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE , context.packageName)
            }
        }
        else {
            Intent().apply {
                action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                data = Uri.fromParts("package" , context.packageName , null)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Opens the system display settings screen.
     *
     * Attempts to launch the system's display settings page. If the intent
     * cannot be resolved, it falls back to the general settings screen.
     *
     * @param context The Android context used to start the activity.
     */
    fun openDisplaySettings(context : Context) {
        val packageManager = context.packageManager

        val displayIntent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        when {
            displayIntent.resolveActivity(packageManager) != null ->
                context.startActivity(displayIntent)
            settingsIntent.resolveActivity(packageManager) != null ->
                context.startActivity(settingsIntent)
        }
    }

    /**
     * Opens the specified application's Play Store page. If the Play Store
     * application is not available, it falls back to opening the web version
     * of the Play Store.
     *
     * @param context The context used to start the intent.
     * @param packageName The package name of the application to display.
     */
    fun openPlayStoreForApp(context : Context , packageName : String) {
        val marketIntent = Intent(
            Intent.ACTION_VIEW , "${AppLinks.MARKET_APP_PAGE}$packageName".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (marketIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(marketIntent)
        }
        else {
            openUrl(context , "${AppLinks.PLAY_STORE_APP}$packageName")
        }
    }

    /**
     * Opens the app's share sheet, allowing users to share a message about the app.
     *
     * This function constructs a share message using a provided string resource and the app's Play Store link.
     * It then creates an ACTION_SEND intent with this message and uses a chooser intent to present the user with options for sharing the message (e.g., email, messaging apps).
     * If the chooser intent is launched without [Intent.FLAG_ACTIVITY_NEW_TASK], the provided context must be an [android.app.Activity].
     *
     * @param context The Android context in which the share sheet should be opened.
     * @param shareMessageFormat The resource ID of the string to be used as the base for the share message. This string should include a placeholder, where the app's playstore link will be injected.
     *                           Example : "Check out this awesome app! Download it here: %s"
     *                           The %s will be replaced by the app's Play Store URL.
     */
    fun shareApp(context : Context , shareMessageFormat : Int) {
        val messageToShare : String = context.getString(shareMessageFormat , "${AppLinks.PLAY_STORE_APP}=${context.packageName}")
        val sendIntent : Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT , messageToShare)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(
            sendIntent , context.resources.getText(R.string.send_email_using)
        )
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * Sends an email to the developer's contact address.
     *
     * A chooser intent is used to let the user select their preferred email client. The chooser is launched with
     * [Intent.FLAG_ACTIVITY_NEW_TASK] to allow starting from a non-activity context. If this flag is removed, an
     * [android.app.Activity] context must be supplied.
     */
    fun sendEmailToDeveloper(context : Context , @StringRes applicationNameRes : Int) {
        val developerEmail = AppLinks.CONTACT_EMAIL

        val appName : String = context.getString(applicationNameRes)
        val subject : String = context.getString(R.string.feedback_for , appName)
        val body : String = context.getString(R.string.dear_developer) + "\n\n"

        val subjectEncoded : String = URLEncoder.encode(subject , "UTF-8").replace("+" , "%20")
        val bodyEncoded : String = URLEncoder.encode(body , "UTF-8").replace("+" , "%20")

        val mailtoUri : Uri = "mailto:$developerEmail?subject=$subjectEncoded&body=$bodyEncoded".toUri()
        val emailIntent = Intent(Intent.ACTION_SENDTO , mailtoUri)

        val chooser = Intent.createChooser(
            emailIntent , context.getString(R.string.send_email_using)
        )
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}