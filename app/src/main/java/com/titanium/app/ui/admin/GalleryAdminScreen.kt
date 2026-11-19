package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.GalleryPhoto
import com.titanium.app.data.firebase.GalleryVideoLink
import com.titanium.app.ui.components.RemoteOrPlaceholderImage
import kotlinx.coroutines.launch

private enum class GalleryAdminTab { PHOTOS, VIDEO_LINKS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    var tab by remember { mutableStateOf(GalleryAdminTab.PHOTOS) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Galería") })

        TabRow(selectedTabIndex = tab.ordinal) {
            Tab(selected = tab == GalleryAdminTab.PHOTOS, onClick = { tab = GalleryAdminTab.PHOTOS }, text = { Text("Fotos") })
            Tab(selected = tab == GalleryAdminTab.VIDEO_LINKS, onClick = { tab = GalleryAdminTab.VIDEO_LINKS }, text = { Text("Vídeos (enlace)") })
        }

        when (tab) {
            GalleryAdminTab.PHOTOS -> GalleryPhotosAdmin(repository)
            GalleryAdminTab.VIDEO_LINKS -> GalleryVideoLinksAdmin(repository)
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}

@Composable
private fun ColumnScope.GalleryPhotosAdmin(repository: FirestoreContentRepository) {
    val photos by repository.observeGalleryPhotos().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Fotos de la galería", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir foto") { showAddDialog = true }
    }

    if (photos.isEmpty()) {
        Text(
            text = "Todavía no se han añadido fotos a la galería.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        items(photos, key = { it.id }) { photo ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    RemoteOrPlaceholderImage(
                        imageUrl = photo.imageUrl,
                        contentDescription = photo.caption,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TranslucentIconButton(
                        icon = Icons.Default.Remove,
                        contentDescription = "Eliminar foto",
                        onClick = { scope.launch { repository.deleteGalleryPhoto(photo.id) } },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    )
                }
                Text(
                    text = photo.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }

    if (showAddDialog) {
        AddPhotoDialog(
            folder = "gallery_photos",
            onDismiss = { showAddDialog = false },
            onAdd = { caption, imageUrl ->
                scope.launch {
                    repository.addGalleryPhoto(imageUrl, caption)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun ColumnScope.GalleryVideoLinksAdmin(repository: FirestoreContentRepository) {
    val links by repository.observeGalleryVideoLinks().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Vídeos (enlace)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir vídeo") { showAddDialog = true }
    }

    if (links.isEmpty()) {
        Text(
            text = "Todavía no se han añadido vídeos a la galería.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        items(links, key = { it.id }) { link ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.6f)) {
                    RemoteOrPlaceholderImage(
                        imageUrl = link.thumbnailImageUrl,
                        contentDescription = link.caption,
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TranslucentIconButton(
                        icon = Icons.Default.Remove,
                        contentDescription = "Eliminar vídeo",
                        onClick = { scope.launch { repository.deleteGalleryVideoLink(link.id) } },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    )
                }
                Text(
                    text = link.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = link.linkUrl,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAddDialog) {
        AddVideoLinkDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { linkUrl, caption, thumbnailUrl ->
                scope.launch {
                    repository.addGalleryVideoLink(linkUrl, caption, thumbnailUrl)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun AddVideoLinkDialog(
    onDismiss: () -> Unit,
    onAdd: (linkUrl: String, caption: String, thumbnailUrl: String?) -> Unit
) {
    var linkUrl by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir vídeo") },
        text = {
            Column {
                Text(
                    text = "Pega el enlace al vídeo ya publicado en Instagram o Facebook (no se sube ningún fichero de vídeo).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("Enlace al vídeo") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Texto descriptivo") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                ImageUploadField(
                    folder = "gallery_video_thumbnails",
                    label = "Miniatura (opcional)",
                    imageUrl = thumbnailUrl,
                    onUploaded = { thumbnailUrl = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = linkUrl.isNotBlank() && caption.isNotBlank(),
                onClick = { onAdd(linkUrl, caption, thumbnailUrl) }
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
