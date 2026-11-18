package com.titanium.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.titanium.app.R
import com.titanium.app.data.firebase.PresentationInfo

/**
 * [onAdminAccessRequested] se dispara al mantener pulsado el logo de la
 * asociación: es el punto de entrada (escondido para el público) al login
 * de administradores. [onLogoPositioned] informa de dónde queda el logo en
 * pantalla una vez colocado, para que la animación de aterrizaje del vídeo
 * de intro (ver HomeScreen) sepa hacia dónde moverse.
 */
@Composable
fun HeaderCard(
    presentation: PresentationInfo,
    onAdminAccessRequested: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoPositioned: (Rect) -> Unit = {}
) {
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
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = { onAdminAccessRequested() })
                    }
            )
            Text(
                text = presentation.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 14.dp)
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
