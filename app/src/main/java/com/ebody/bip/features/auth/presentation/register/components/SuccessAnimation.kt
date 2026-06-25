package com.ebody.bip.features.auth.presentation.register.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ebody.bip.R

@Composable
fun SuccessAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}