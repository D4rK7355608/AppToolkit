package com.d4rk.android.libs.apptoolkit.ui.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.d4rk.android.libs.apptoolkit.ui.components.modifiers.bounceClick

@Composable
fun SmallFloatingActionButton(isVisible : Boolean , isExtended : Boolean , icon : ImageVector , contentDescription : String? = null , onClick : () -> Unit) {
    AnimatedVisibility(
        visible = isVisible && isExtended ,
        enter = scaleIn() ,
        exit = scaleOut() ,
    ) {
        SmallFloatingActionButton(onClick = onClick , modifier = Modifier
                .padding(bottom = 12.dp)
                .bounceClick()) {
            Icon(imageVector = icon , contentDescription = contentDescription)
        }
    }
}
