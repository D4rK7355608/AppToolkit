package com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d4rk.android.libs.apptoolkit.R
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.LargeVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

object FinalOnboardingKonfettiState {
    var hasKonfettiBeenShownGlobally by mutableStateOf(false)
}

@Composable
fun FinalOnboardingPageTab() {

    var showKonfettiAnimationForThisInstance by remember { mutableStateOf(false) }
    var iconVisible by remember { mutableStateOf(false) } // State to control icon animation

    val party = Party(speed = 0f , maxSpeed = 30f , damping = 0.9f , spread = Spread.ROUND , position = Position.Relative(0.5 , 0.3) , emitter = Emitter(duration = 200 , TimeUnit.MILLISECONDS).max(100))
    val partyRain = Party(emitter = Emitter(duration = 3 , TimeUnit.SECONDS).perSecond(60) , angle = Angle.BOTTOM , spread = Spread.SMALL , speed = 5f , maxSpeed = 15f , timeToLive = 3000L , position = Position.Relative(0.0 , 0.0).between(Position.Relative(1.0 , 0.0)))

    // Animation for icon scale (settle back) - Scale down from 1.2 to 1
    val iconSettleScale by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 1.2f, // Settle back to 1
        animationSpec = tween(durationMillis = 200, delayMillis = 400), label = "Icon Settle Scale" // Delay to start after initial scale up
    )

    // Animation for icon alpha (fade-in)
    val iconAlpha by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = "Icon Alpha"
    )

    // Animation for icon translationY (bounce)
    val iconTranslateY by animateFloatAsState(
        targetValue = if (iconVisible) 0f else 30f, // Start slightly lower and move up
        animationSpec = tween(durationMillis = 400, delayMillis = 200), label = "Icon TranslateY" // Delay bounce slightly
    )

    LaunchedEffect(Unit) {
        if (! FinalOnboardingKonfettiState.hasKonfettiBeenShownGlobally) {
            iconVisible = true // Trigger icon animation
            delay(300)
            showKonfettiAnimationForThisInstance = true
            FinalOnboardingKonfettiState.hasKonfettiBeenShownGlobally = true
            delay(4000)
        } else {
            // If konfetti has been shown, just make the icon visible without animation
            // or with a faster, simpler animation if desired.
            iconVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp , vertical = SizeConstants.ExtraLargeIncreasedSize) , horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle ,
                contentDescription = stringResource(R.string.onboarding_complete_icon_desc) ,
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = iconSettleScale // Applies the settle scale
                        scaleY = iconSettleScale // Applies the settle scale
                        translationY = iconTranslateY // Applies the bounce
                        alpha = iconAlpha
                    }
                ,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(R.string.onboarding_final_title) , style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold , fontSize = 30.sp , textAlign = TextAlign.Center) , color = MaterialTheme.colorScheme.onSurface)
            LargeVerticalSpacer()
            Text(text = stringResource(R.string.onboarding_final_description) , style = MaterialTheme.typography.bodyLarge , textAlign = TextAlign.Center , color = MaterialTheme.colorScheme.onSurfaceVariant , modifier = Modifier.padding(horizontal = SizeConstants.LargeSize))

        }

        if (showKonfettiAnimationForThisInstance) {
            KonfettiView(
                modifier = Modifier.fillMaxSize() ,
                parties = listOf(party , partyRain) ,
            )
        }
    }
}