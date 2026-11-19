package com.titanium.app.ui.components

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.titanium.app.data.firebase.GalleryPhoto
import com.titanium.app.data.firebase.GalleryVideoLink
import com.titanium.app.ui.admin.TranslucentIconButton

private enum class GalleryTab(val label: String) {
    PHOTOS("Fotos"),
    VIDEOS("Vídeos")
}

private const val GALLERY_COLUMNS = 2

@Composable
fun GallerySection(
    photos: List<GalleryPhoto>,
    videoLinks: List<GalleryVideoLink>,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onAddPhotoRequested: () -> Unit = {},
    onDeletePhoto: (String) -> Unit = {},
    onAddVideoRequested: () -> Unit = {},
    onDeleteVideo: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(GalleryTab.PHOTOS) }

    Column(modifier = modifier) {
        SectionTitle(text = "Galería")

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            ) {
                GalleryTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.label) }
                    )
                }
            }
            if (isAdmin) {
                TranslucentIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = if (selectedTab == GalleryTab.PHOTOS) "Añadir foto" else "Añadir vídeo",
                    onClick = { if (selectedTab == GalleryTab.PHOTOS) onAddPhotoRequested() else onAddVideoRequested() },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        when (selectedTab) {
            GalleryTab.PHOTOS -> PhotoGrid(photos = photos, isAdmin = isAdmin, onDelete = onDeletePhoto)
            GalleryTab.VIDEOS -> VideoLinkGrid(videoLinks = videoLinks, isAdmin = isAdmin, onDelete = onDeleteVideo)
        }
    }
}

/**
 * Cuadrícula "plana" (sin scroll propio): esta sección ya vive dentro del
 * LazyColumn de la pantalla principal, así que anidar aquí un
 * LazyVerticalGrid (que también es scrollable) provoca un
 * IllegalStateException por restricciones de altura infinita. Al ser
 * listas de contenido acotadas, basta con repartirlas en filas normales.
 */
@Composable
private fun PhotoGrid(photos: List<GalleryPhoto>, isAdmin: Boolean, onDelete: (String) -> Unit) {
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
                        Box(modifier = Modifier.fillMaxWidth()) {
                            RemoteOrPlaceholderImage(
                                imageUrl = photo.imageUrl,
                                contentDescription = photo.caption,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                            if (isAdmin) {
                                TranslucentIconButton(
                                    icon = Icons.Default.Remove,
                                    contentDescription = "Eliminar foto",
                                    onClick = { onDelete(photo.id) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                                )
                            }
                        }
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

/**
 * Los vídeos ya no se alojan en la app: cada tarjeta abre el enlace a la
 * publicación de Instagram/Facebook donde está subido el vídeo.
 */
@Composable
private fun VideoLinkGrid(videoLinks: List<GalleryVideoLink>, isAdmin: Boolean, onDelete: (String) -> Unit) {
    if (videoLinks.isEmpty()) {
        EmptyGalleryMessage("Todavía no se han añadido vídeos a la galería.")
        return
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        videoLinks.chunked(GALLERY_COLUMNS).forEach { rowLinks ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowLinks.forEach { link ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, link.linkUrl.toUri()))
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            RemoteOrPlaceholderImage(
                                imageUrl = link.thumbnailImageUrl,
                                contentDescription = link.caption,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Abrir vídeo",
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(8.dp)
                            )
                            if (isAdmin) {
                                TranslucentIconButton(
                                    icon = Icons.Default.Remove,
                                    contentDescription = "Eliminar vídeo",
                                    onClick = { onDelete(link.id) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                                )
                            }
                        }
                        Text(
                            text = link.caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                }
                repeat(GALLERY_COLUMNS - rowLinks.size) {
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
