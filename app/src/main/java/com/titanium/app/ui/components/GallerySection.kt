package com.titanium.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.titanium.app.data.GalleryContent
import com.titanium.app.data.GalleryPhoto
import com.titanium.app.data.GalleryVideo

private enum class GalleryTab(val label: String) {
    PHOTOS("Fotos"),
    VIDEOS("Vídeos")
}

private const val GALLERY_COLUMNS = 2

@Composable
fun GallerySection(gallery: GalleryContent, modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(GalleryTab.PHOTOS) }
    var openVideo by remember { mutableStateOf<GalleryVideo?>(null) }

    Column(modifier = modifier) {
        SectionTitle(text = "Galería")

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            GalleryTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.label) }
                )
            }
        }

        when (selectedTab) {
            GalleryTab.PHOTOS -> PhotoGrid(photos = gallery.photos)
            GalleryTab.VIDEOS -> VideoGrid(videos = gallery.videos, onVideoClick = { openVideo = it })
        }
    }

    val video = openVideo
    if (video != null) {
        FullscreenVideoDialog(
            videoAssetPath = "videos/${video.videoAsset}",
            caption = video.caption,
            onDismiss = { openVideo = null }
        )
    }
}

/**
 * Cuadrícula "plana" (sin scroll propio): esta sección ya vive dentro del
 * LazyColumn de la pantalla principal, así que anidar aquí un
 * LazyVerticalGrid (que también es scrollable) provoca un
 * IllegalStateException por restricciones de altura infinita. Al ser
 * listas de contenido acotadas (fotos/vídeos de la galería), basta con
 * repartirlas en filas normales.
 */
@Composable
private fun PhotoGrid(photos: List<GalleryPhoto>) {
    if (photos.isEmpty()) {
        EmptyGalleryMessage("Todavía no se han añadido fotos a la galería.")
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        photos.chunked(GALLERY_COLUMNS).forEach { rowPhotos ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowPhotos.forEach { photo ->
                    Column(modifier = Modifier.weight(1f)) {
                        AssetOrPlaceholderImage(
                            drawableName = photo.drawable,
                            contentDescription = photo.caption,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        Text(
                            text = photo.caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                repeat(GALLERY_COLUMNS - rowPhotos.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun VideoGrid(videos: List<GalleryVideo>, onVideoClick: (GalleryVideo) -> Unit) {
    if (videos.isEmpty()) {
        EmptyGalleryMessage("Todavía no se han añadido vídeos a la galería.")
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        videos.chunked(GALLERY_COLUMNS).forEach { rowVideos ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowVideos.forEach { video ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onVideoClick(video) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            AssetOrPlaceholderImage(
                                drawableName = video.thumbnailDrawable,
                                contentDescription = video.caption,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Reproducir vídeo",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(8.dp)
                            )
                        }
                        Text(
                            text = video.caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                repeat(GALLERY_COLUMNS - rowVideos.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EmptyGalleryMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}
