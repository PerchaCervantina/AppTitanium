package com.titanium.app.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.titanium.app.R
import com.titanium.app.data.firebase.PresentationInfo
import com.titanium.app.ui.admin.TranslucentIconButton
import java.net.URLEncoder

/**
 * [onAdminAccessRequested] se dispara al mantener pulsado el logo de la
 * asociación (solo si todavía no se ha iniciado sesión): es el punto de
 * entrada escondido al login de administradores. [onLogoPositioned]
 * informa de dónde queda el logo en pantalla, para la animación de
 * aterrizaje del vídeo de intro (ver HomeScreen). En modo administrador
 * ([isAdmin]) aparece un lápiz para editar el texto y la localización en
 * el sitio.
 */
@Composable
fun HeaderCard(
    presentation: PresentationInfo,
    onAdminAccessRequested: () -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onSavePresentation: (PresentationInfo) -> Unit = {},
    onLogoPositioned: (Rect) -> Unit = {}
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Logo fijo de la asociación: no es editable desde el panel de
            // administración (a diferencia del resto de esta cabecera).
            Image(
                painter = painterResource(R.drawable.logo_titanium),
                contentDescription = "Titanium",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .onGloballyPositioned { onLogoPositioned(it.boundsInRoot()) }
                    .pointerInput(isAdmin) {
                        if (!isAdmin) {
                            detectTapGestures(onLongPress = { onAdminAccessRequested() })
                        }
                    }
            )

            if (editing) {
                PresentationEditForm(
                    initial = presentation,
                    onSave = { onSavePresentation(it); editing = false },
                    onCancel = { editing = false }
                )
                return@Column
            }

            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = presentation.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (isAdmin) {
                    TranslucentIconButton(icon = Icons.Default.Edit, contentDescription = "Editar presentación", onClick = { editing = true })
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clickable(enabled = presentation.location.isNotBlank()) {
                        val query = URLEncoder.encode(presentation.location, "UTF-8")
                        val uri = "https://www.google.com/maps/search/?api=1&query=$query".toUri()
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = presentation.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PresentationEditForm(
    initial: PresentationInfo,
    onSave: (PresentationInfo) -> Unit,
    onCancel: () -> Unit
) {
    var description by remember { mutableStateOf(initial.description) }
    var location by remember { mutableStateOf(initial.location) }

    Column(modifier = Modifier.padding(top = 12.dp)) {
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Texto de presentación") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Localización") },
            supportingText = { Text("Se usa tal cual para abrir Google Maps.") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Row(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedButton(onClick = { onSave(PresentationInfo(description, location)) }) { Text("Guardar") }
            TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    }
}
