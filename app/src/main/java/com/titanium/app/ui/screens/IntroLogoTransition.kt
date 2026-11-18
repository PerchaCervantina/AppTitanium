package com.titanium.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.titanium.app.R
import kotlin.math.roundToInt

// Proporción ancho/alto del vídeo de intro (540x960, formato vertical de
// móvil) y, dentro de él, el rectángulo donde aparece el logo en el último
// fotograma (calculado una vez analizando ese fotograma). Con esto se
// calcula de dónde "sale" el logo al terminar el vídeo, sin tener que medir
// nada en tiempo real sobre el propio vídeo.
private const val VIDEO_ASPECT = 540f / 960f
private const val LOGO_FRACTION_LEFT = 0.076f
private const val LOGO_FRACTION_TOP = 0.385f
private const val LOGO_FRACTION_WIDTH = 0.846f
private const val LOGO_FRACTION_HEIGHT = 0.220f

/** Relación ancho/alto del propio PNG del logo (res/drawable-nodpi/logo_titanium.png). */
private const val LOGO_IMAGE_ASPECT = 523f / 382f

/**
 * Punto de partida del logo (en píxeles de pantalla) justo cuando termina
 * el vídeo, asumiendo que el PlayerView lo ha mostrado ajustado (sin
 * recortar) dentro de una pantalla de [screenWidthPx] x [screenHeightPx].
 */
fun introLogoStartRect(screenWidthPx: Float, screenHeightPx: Float): Rect {
    val screenAspect = screenWidthPx / screenHeightPx

    val videoWidthPx: Float
    val videoHeightPx: Float
    val videoLeft: Float
    val videoTop: Float
    if (screenAspect > VIDEO_ASPECT) {
        videoHeightPx = screenHeightPx
        videoWidthPx = videoHeightPx * VIDEO_ASPECT
        videoLeft = (screenWidthPx - videoWidthPx) / 2f
        videoTop = 0f
    } else {
        videoWidthPx = screenWidthPx
        videoHeightPx = videoWidthPx / VIDEO_ASPECT
        videoLeft = 0f
        videoTop = (screenHeightPx - videoHeightPx) / 2f
    }

    val left = videoLeft + LOGO_FRACTION_LEFT * videoWidthPx
    val top = videoTop + LOGO_FRACTION_TOP * videoHeightPx
    val width = LOGO_FRACTION_WIDTH * videoWidthPx
    val height = LOGO_FRACTION_HEIGHT * videoHeightPx
    return Rect(left, top, left + width, top + height)
}

/**
 * Logo flotante que se anima desde [startRect] (donde queda al terminar el
 * vídeo) hasta [targetRect] (su sitio real en la cabecera), con una
 * pequeña "caída y subida" en vez de una línea recta. Al terminar, deja el
 * resto del contenido real apareciendo con un fundido ([contentAlpha])
 * mientras el logo se queda quieto en su sitio final, para que el cambio
 * del logo flotante al logo real sea invisible.
 */
@Composable
fun IntroLogoTransition(
    startRect: Rect,
    targetRect: Rect,
    contentAlpha: Animatable<Float, *>,
    onSettled: () -> Unit
) {
    val density = LocalDensity.current
    val dipPx = with(density) { 26.dp.toPx() }

    val center = remember { Animatable(startRect.center, Offset.VectorConverter) }
    val width = remember { Animatable(startRect.width) }

    LaunchedEffect(targetRect) {
        val midCenter = Offset(
            x = startRect.center.x + (targetRect.center.x - startRect.center.x) * 0.35f,
            y = startRect.center.y + dipPx
        )
        center.animateTo(
            targetValue = targetRect.center,
            animationSpec = keyframes {
                durationMillis = 650
                startRect.center at 0
                midCenter at 260 using FastOutSlowInEasing
                targetRect.center at 650 using FastOutSlowInEasing
            }
        )
    }

    LaunchedEffect(targetRect) {
        width.animateTo(targetRect.width, animationSpec = tween(650, easing = FastOutSlowInEasing))
        contentAlpha.animateTo(1f, animationSpec = tween(300))
        onSettled()
    }

    val heightPx = width.value / LOGO_IMAGE_ASPECT
    val widthDp = with(density) { width.value.toDp() }
    val heightDp = with(density) { heightPx.toDp() }

    Image(
        painter = painterResource(R.drawable.logo_titanium),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(widthDp, heightDp)
            .offset {
                IntOffset(
                    x = (center.value.x - width.value / 2f).roundToInt(),
                    y = (center.value.y - heightPx / 2f).roundToInt()
                )
            }
    )
}
