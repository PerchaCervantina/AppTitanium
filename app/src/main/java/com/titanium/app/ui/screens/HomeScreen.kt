package com.titanium.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.EventEntry
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.GalleryPhoto
import com.titanium.app.data.firebase.GalleryVideoLink
import com.titanium.app.data.firebase.MembershipInfo
import com.titanium.app.data.firebase.PresentationInfo
import com.titanium.app.data.firebase.VenuePhoto
import com.titanium.app.ui.components.EventCard
import com.titanium.app.ui.components.GallerySection
import com.titanium.app.ui.components.HeaderCard
import com.titanium.app.ui.components.IntroVideoPlayer
import com.titanium.app.ui.components.MembershipSection
import com.titanium.app.ui.components.SectionTitle
import com.titanium.app.ui.components.VenueSection
import com.titanium.app.util.assetExists

private const val INTRO_VIDEO_ASSET = "videos/intro.mp4"

private enum class IntroPhase { VIDEO, LOGO_TRANSITION, DONE }

/**
 * Pantalla principal: vídeo de introducción a pantalla completa que, al
 * terminar, da paso a una pequeña animación en la que el logo del vídeo
 * "aterriza" en su sitio de la cabecera (con una caída y subida, en vez de
 * un simple fundido), y después aparece el resto del contenido, leído en
 * tiempo real desde Firestore. Si no hay vídeo de intro, se salta
 * directamente al contenido.
 */
@Composable
fun HomeScreen(onAdminAccessRequested: () -> Unit) {
    val context = LocalContext.current
    val hasIntroVideo = remember { assetExists(context, INTRO_VIDEO_ASSET) }

    var phase by remember { mutableStateOf(if (hasIntroVideo) IntroPhase.VIDEO else IntroPhase.DONE) }
    val contentAlpha = remember { Animatable(if (hasIntroVideo) 0f else 1f) }
    var logoTargetRect by remember { mutableStateOf<Rect?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // El contenido real se compone desde el principio (para poder
        // medir dónde cae el logo de la cabecera) pero permanece invisible
        // hasta que termina la animación de aterrizaje.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha.value }
        ) {
            MainContent(
                onAdminAccessRequested = onAdminAccessRequested,
                onHeaderLogoPositioned = { logoTargetRect = it }
            )
        }

        when (phase) {
            IntroPhase.VIDEO -> IntroVideoPlayer(
                assetPath = INTRO_VIDEO_ASSET,
                onFinished = { phase = IntroPhase.LOGO_TRANSITION }
            )

            IntroPhase.LOGO_TRANSITION -> {
                val target = logoTargetRect
                if (target != null) {
                    IntroLogoTransition(
                        startRect = introLogoStartRect(screenWidthPx, screenHeightPx),
                        targetRect = target,
                        contentAlpha = contentAlpha,
                        onSettled = { phase = IntroPhase.DONE }
                    )
                } else {
                    // No debería pasar (el contenido se mide casi al
                    // instante), pero por si acaso no nos quedamos
                    // atascados sin mostrar nada.
                    LaunchedEffect(Unit) {
                        contentAlpha.snapTo(1f)
                        phase = IntroPhase.DONE
                    }
                }
            }

            IntroPhase.DONE -> Unit
        }
    }
}

@Composable
private fun MainContent(
    onAdminAccessRequested: () -> Unit,
    onHeaderLogoPositioned: (Rect) -> Unit,
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() }
) {
    val presentation by repository.observePresentation().collectAsState(initial = PresentationInfo())
    val events by repository.observeEvents().collectAsState(initial = emptyList<EventEntry>())
    val venuePhotos by repository.observeVenuePhotos().collectAsState(initial = emptyList<VenuePhoto>())
    val galleryPhotos by repository.observeGalleryPhotos().collectAsState(initial = emptyList<GalleryPhoto>())
    val galleryVideoLinks by repository.observeGalleryVideoLinks().collectAsState(initial = emptyList<GalleryVideoLink>())
    val membership by repository.observeMembership().collectAsState(initial = MembershipInfo())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HeaderCard(
                presentation = presentation,
                onAdminAccessRequested = onAdminAccessRequested,
                onLogoPositioned = onHeaderLogoPositioned
            )
        }

        item { SectionTitle(text = "Próximos eventos") }
        items(events, key = { it.id }) { event -> EventCard(event = event) }

        item {
            VenueSection(images = venuePhotos, modifier = Modifier.padding(top = 8.dp))
        }

        item {
            GallerySection(
                photos = galleryPhotos,
                videoLinks = galleryVideoLinks,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item { MembershipSection(membership = membership, modifier = Modifier.padding(top = 8.dp)) }

        item { Box(modifier = Modifier.height(32.dp)) }
    }
}
