package com.titanium.app.ui.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.FirestoreContentRepository
import kotlinx.coroutines.launch

/**
 * Botón para elegir una imagen de la galería del móvil y subirla a
 * Firebase Storage. Mientras sube, muestra un indicador de carga; cuando
 * termina, llama a [onUploaded] con la URL resultante para guardarla junto
 * con el resto del formulario.
 */
@Composable
fun ImageUploadField(
    folder: String,
    label: String,
    imageUrl: String?,
    onUploaded: (String) -> Unit,
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() }
) {
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            isUploading = true
            errorMessage = null
            scope.launch {
                try {
                    val url = repository.uploadImage(uri, folder)
                    onUploaded(url)
                } catch (e: Exception) {
                    errorMessage = "No se ha podido subir la imagen: ${e.message}"
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        OutlinedButton(
            enabled = !isUploading,
            onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        ) {
            Text(if (imageUrl.isNullOrBlank()) "$label: elegir imagen" else "$label: cambiar imagen")
        }

        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
        }

        if (!imageUrl.isNullOrBlank()) {
            Text(
                text = "Imagen lista.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
