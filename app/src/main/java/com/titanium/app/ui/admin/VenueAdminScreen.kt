package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.VenuePhoto
import com.titanium.app.ui.components.RemoteOrPlaceholderImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    val photos by repository.observeVenuePhotos().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nuestra sede") })

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Fotos de la sede", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir foto") { showAddDialog = true }
        }

        if (photos.isEmpty()) {
            Text(
                text = "Todavía no se han añadido fotos de la sede.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(photos, key = { it.id }) { photo ->
                VenuePhotoItem(
                    photo = photo,
                    onDelete = { scope.launch { repository.deleteVenuePhoto(photo.id) } }
                )
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }

    if (showAddDialog) {
        AddPhotoDialog(
            folder = "venue",
            onDismiss = { showAddDialog = false },
            onAdd = { caption, imageUrl ->
                scope.launch {
                    repository.addVenuePhoto(imageUrl, caption)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
private fun VenuePhotoItem(photo: VenuePhoto, onDelete: () -> Unit) {
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
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
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
