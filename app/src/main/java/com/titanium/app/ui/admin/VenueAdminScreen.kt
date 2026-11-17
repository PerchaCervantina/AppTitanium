package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.VenuePhoto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    val photos by repository.observeVenuePhotos().collectAsState(initial = emptyList())
    var caption by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Nuestra sede") })

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
                        folder = "venue",
                        label = "Foto de la sede",
                        imageUrl = imageUrl,
                        onUploaded = { imageUrl = it }
                    )
                    OutlinedButton(
                        enabled = caption.isNotBlank() && !imageUrl.isNullOrBlank(),
                        onClick = {
                            val url = imageUrl.orEmpty()
                            val text = caption
                            scope.launch {
                                repository.addVenuePhoto(url, text)
                                caption = ""
                                imageUrl = null
                            }
                        },
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
                    ) {
                        Text("Añadir foto")
                    }
                    HorizontalDivider()
                }
            }
            items(photos) { photo ->
                VenuePhotoRow(photo = photo, onDelete = { scope.launch { repository.deleteVenuePhoto(photo.id) } })
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}

@Composable
private fun VenuePhotoRow(photo: VenuePhoto, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(
                text = photo.caption,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDelete) { Text("Eliminar") }
        }
    }
}
