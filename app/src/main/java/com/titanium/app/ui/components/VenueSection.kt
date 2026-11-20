package com.titanium.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.VenuePhoto
import com.titanium.app.ui.admin.TranslucentIconButton

@Composable
fun VenueSection(
    images: List<VenuePhoto>,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onAddRequested: () -> Unit = {},
    onDelete: (String) -> Unit = {}
) {
    var zoomedImageUrl by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        SectionTitle(
            text = "Nuestra sede",
            trailing = if (isAdmin) {
                { TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir foto", onClick = onAddRequested) }
            } else null
        )

        if (images.isEmpty()) {
            Text(
                text = "Todavía no se han añadido fotos de la sede.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(images, key = { it.id }) { image ->
                    Column(modifier = Modifier.width(220.dp)) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            RemoteOrPlaceholderImage(
                                imageUrl = image.imageUrl,
                                contentDescription = image.caption,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clickable(enabled = image.imageUrl.isNotBlank()) { zoomedImageUrl = image.imageUrl }
                            )
                            if (isAdmin) {
                                TranslucentIconButton(
                                    icon = Icons.Default.Remove,
                                    contentDescription = "Eliminar foto",
                                    onClick = { onDelete(image.id) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                                )
                            }
                        }
                        Text(
                            text = image.caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    zoomedImageUrl?.let { url ->
        ZoomableImageOverlay(
            imageUrl = url,
            contentDescription = "Foto de la sede",
            onDismiss = { zoomedImageUrl = null }
        )
    }
}
