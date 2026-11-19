package com.titanium.app.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.titanium.app.data.cloudinary.CloudinaryRepository
import com.titanium.app.ui.components.RemoteOrPlaceholderImage
import kotlinx.coroutines.launch

/** Botoncito circular translúcido para "+"/"−" sobre fotos y títulos de sección. */
@Composable
fun TranslucentIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.White)
    }
}

/** Diálogo simple para añadir una foto (sede o galería): texto + imagen. */
@Composable
fun AddPhotoDialog(folder: String, onDismiss: () -> Unit, onAdd: (caption: String, imageUrl: String) -> Unit) {
    var caption by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir foto") },
        text = {
            Column {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Texto de la foto") },
                    modifier = Modifier.fillMaxWidth()
                )
                ImageUploadField(folder = folder, label = "Foto", imageUrl = imageUrl, onUploaded = { imageUrl = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = caption.isNotBlank() && !imageUrl.isNullOrBlank(),
                onClick = { onAdd(caption, imageUrl.orEmpty()) }
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

/**
 * Igual que [RemoteOrPlaceholderImage], pero se puede tocar para elegir y
 * subir otra imagen en su lugar (usado en la edición de eventos, para
 * poder cambiar el cartel tocándolo directamente).
 */
@Composable
fun TappableImageUpload(
    imageUrl: String?,
    folder: String,
    contentDescription: String?,
    onUploaded: (String) -> Unit,
    modifier: Modifier = Modifier,
    repository: CloudinaryRepository = remember { CloudinaryRepository() }
) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            isUploading = true
            scope.launch {
                try {
                    onUploaded(repository.uploadImage(context, uri, folder))
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (imageUrl.isNullOrBlank()) 1.4f else 1f)
            .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    ) {
        RemoteOrPlaceholderImage(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
        if (isUploading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        Text(
            text = if (imageUrl.isNullOrBlank()) "Toca para elegir imagen" else "Toca para cambiar la imagen",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        )
    }
}

/** Meses abreviados en español, por índice de Calendar.MONTH (0 = enero). */
val MONTH_ABBREVIATIONS = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")

/** Diálogo para añadir un vídeo de galería: solo el enlace, texto y miniatura opcional. */
@Composable
fun AddVideoLinkDialog(
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
