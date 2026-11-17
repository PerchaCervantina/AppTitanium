package com.titanium.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

/**
 * Pantalla principal: vídeo de introducción a pantalla completa que, al
 * terminar (o si no hay vídeo disponible), se funde con el resto del
 * contenido de la asociación, leído en tiempo real desde Firestore.
 */
@Composable
fun HomeScreen(onAdminAccessRequested: () -> Unit) {
    val context = LocalContext.current
    var showIntro by remember { mutableStateOf(assetExists(context, INTRO_VIDEO_ASSET)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Crossfade(
            targetState = showIntro,
            animationSpec = tween(durationMillis = 700),
            label = "intro-to-content"
        ) { introVisible ->
            if (introVisible) {
                IntroVideoPlayer(
                    assetPath = INTRO_VIDEO_ASSET,
                    onFinished = { showIntro = false }
                )
            } else {
                MainContent(onAdminAccessRequested = onAdminAccessRequested)
            }
        }
    }
}

@Composable
private fun MainContent(
    onAdminAccessRequested: () -> Unit,
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
            HeaderCard(presentation = presentation, onAdminAccessRequested = onAdminAccessRequested)
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
