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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.titanium.app.data.AppContent
import com.titanium.app.data.ContentRepository
import com.titanium.app.ui.components.EventCard
import com.titanium.app.ui.components.GallerySection
import com.titanium.app.ui.components.HeaderCard
import com.titanium.app.ui.components.IntroVideoPlayer
import com.titanium.app.ui.components.MembershipSection
import com.titanium.app.ui.components.SectionTitle
import com.titanium.app.ui.components.VenueSection

/**
 * Pantalla principal: vídeo de introducción a pantalla completa que, al
 * terminar (o si no hay vídeo disponible), se funde con el resto del
 * contenido de la asociación.
 */
@Composable
fun HomeScreen(content: AppContent) {
    val context = LocalContext.current
    var showIntro by remember {
        mutableStateOf(ContentRepository.assetExists(context, "videos/${content.intro.videoAsset}"))
    }

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
                    assetPath = "videos/${content.intro.videoAsset}",
                    onFinished = { showIntro = false }
                )
            } else {
                MainContent(content = content)
            }
        }
    }
}

@Composable
private fun MainContent(content: AppContent) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { HeaderCard(presentation = content.presentation) }

        item { SectionTitle(text = "Próximos eventos") }
        items(content.events) { event -> EventCard(event = event) }

        item { VenueSection(images = content.venue.images, modifier = Modifier.padding(top = 8.dp)) }

        item { GallerySection(gallery = content.gallery, modifier = Modifier.padding(top = 8.dp)) }

        item { MembershipSection(membership = content.membership, modifier = Modifier.padding(top = 8.dp)) }

        item { Box(modifier = Modifier.height(32.dp)) }
    }
}
