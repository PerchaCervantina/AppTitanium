package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.GalleryPhoto
import com.titanium.app.data.firebase.GalleryVideoLink
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
    var caption by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Texto de la foto") },
                    modifier = Modifier.fillMaxWidth()
                )
                ImageUploadField(
                    folder = "gallery_photos",
                    label = "Foto",
                    imageUrl = imageUrl,
                    onUploaded = { imageUrl = it }
                )
                OutlinedButton(
                    enabled = caption.isNotBlank() && !imageUrl.isNullOrBlank(),
                    onClick = {
                        val url = imageUrl.orEmpty()
                        val text = caption
                        scope.launch {
                            repository.addGalleryPhoto(url, text)
                            caption = ""
                            imageUrl = null
                        }
                    },
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                ) { Text("Añadir foto") }
                HorizontalDivider()
            }
        }
        items(photos) { photo -> GalleryPhotoRow(photo) { scope.launch { repository.deleteGalleryPhoto(photo.id) } } }
    }
}

@Composable
private fun GalleryPhotoRow(photo: GalleryPhoto, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(text = photo.caption, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = onDelete) { Text("Eliminar") }
        }
    }
}

@Composable
private fun ColumnScope.GalleryVideoLinksAdmin(repository: FirestoreContentRepository) {
    val links by repository.observeGalleryVideoLinks().collectAsState(initial = emptyList())
    var linkUrl by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "Pega aquí el enlace al vídeo ya publicado en Instagram o Facebook " +
                        "(no se sube ningún fichero de vídeo, solo el enlace).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("Enlace al vídeo (Instagram/Facebook)") },
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
                OutlinedButton(
                    enabled = linkUrl.isNotBlank() && caption.isNotBlank(),
                    onClick = {
                        val url = linkUrl
                        val text = caption
                        val thumb = thumbnailUrl
                        scope.launch {
                            repository.addGalleryVideoLink(url, text, thumb)
                            linkUrl = ""
                            caption = ""
                            thumbnailUrl = null
                        }
                    },
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                ) { Text("Añadir enlace de vídeo") }
                HorizontalDivider()
            }
        }
        items(links) { link ->
            GalleryVideoLinkRow(link) { scope.launch { repository.deleteGalleryVideoLink(link.id) } }
        }
    }
}

@Composable
private fun GalleryVideoLinkRow(link: GalleryVideoLink, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = link.caption, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = link.linkUrl,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onDelete) { Text("Eliminar") }
        }
    }
}
