package com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.sections

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.ui.components.modifiers.bounceClick
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.android.libs.apptoolkit.ui.components.spacers.MediumVerticalSpacer

/**
 * Displays an information message section with an optional "Learn More" link.
 *
 * This composable displays a section containing:
 * - An info icon.
 * - A text message.
 * - Optionally, a clickable "Learn More" link that opens a specified URL.
 *
 * @param message The main message to be displayed.
 * @param modifier The modifier to be applied to the container (Column).
 * @param learnMoreText The text to be displayed for the "Learn More" link. If null or empty, the link will not be shown.
 * @param learnMoreUrl The URL to be opened when the "Learn More" link is clicked. If null or empty, the link will not be shown.
 *
 * Example Usage:
 * ```
 * InfoMessageSection(
 *     message = "This is an important information message.",
 *     modifier = Modifier.padding(16.dp),
 *     learnMoreText = "Learn More",
 *     learnMoreUrl = "https://www.example.com"
 * )
 *
 * InfoMessageSection(
 *     message = "Another message without a learn more link",
 */
@Composable
fun InfoMessageSection(message : String , modifier : Modifier = Modifier , learnMoreText : String? = null , learnMoreUrl : String? = null) {
    val context : Context = LocalContext.current

    Column(modifier = modifier) {
        Icon(imageVector = Icons.Outlined.Info , contentDescription = stringResource(id = R.string.about))
        MediumVerticalSpacer()
        Text(text = message , style = MaterialTheme.typography.bodyMedium)

        if (! learnMoreText.isNullOrEmpty() && ! learnMoreUrl.isNullOrEmpty()) {
            val annotatedString : AnnotatedString = buildAnnotatedString {
                val startIndex = length
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary , textDecoration = TextDecoration.Underline)) {
                    append(learnMoreText)
                }
                val endIndex : Int = length

                addStringAnnotation(tag = "URL" , annotation = learnMoreUrl , start = startIndex , end = endIndex)
            }

            Text(text = annotatedString , modifier = Modifier
                    .bounceClick()
                    .clickable {
                        annotatedString.getStringAnnotations(
                            tag = "URL" , start = 0 , end = annotatedString.length
                        ).firstOrNull()?.let { annotation ->
                            IntentsHelper.openUrl(context = context , url = annotation.item)
                        }
                    })
        }
    }
}