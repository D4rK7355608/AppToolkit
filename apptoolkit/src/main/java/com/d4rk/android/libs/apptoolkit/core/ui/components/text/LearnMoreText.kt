package com.d4rk.android.libs.apptoolkit.core.ui.components.text

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun LearnMoreText(text : String = stringResource(R.string.learn_more) , url : String , modifier : Modifier = Modifier) {
    val context : Context = LocalContext.current
    val textColor : Color = MaterialTheme.colorScheme.primary
    val annotatedString : AnnotatedString = remember(key1 = text , key2 = url) {
        buildAnnotatedString {
            val start : Int = length
            withStyle(style = SpanStyle(color = textColor , textDecoration = TextDecoration.Underline)) {
                append(text = text)
            }
            addStringAnnotation(tag = "URL" , annotation = url , start = start , end = length)
        }
    }

    Text(
        text = annotatedString , modifier = modifier
                .bounceClick()
                .clickable {
                    annotatedString.getStringAnnotations(tag = "URL" , start = 0 , end = annotatedString.length).firstOrNull()?.let { annotation : AnnotatedString.Range<String> ->
                        IntentsHelper.openUrl(context = context , url = annotation.item)
                    }
                })
}