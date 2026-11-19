package com.titanium.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Título de sección con el estilo "cartel" de la app (texto en mayúsculas).
 */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

/**
 * Muestra la imagen de la URL de Firebase Storage que haya subido un
 * administrador. Si todavía no hay imagen (null/vacía), muestra un icono
 * comodín en su lugar, para que nada se rompa mientras se va completando
 * el contenido.
 */
@Composable
fun RemoteOrPlaceholderImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageUrl.isNullOrBlank()) {
        androidx.compose.foundation.layout.Box(
            // heightIn(min = ...) por si el modifier del sitio donde se usa
            // no fija ninguna altura (p. ej. carteles de evento, que se
            // dimensionan solos según la imagen real una vez subida): sin
            // esto, el icono "sin imagen" quedaría con alto 0 y sería invisible.
            modifier = Modifier
                .heightIn(min = 160.dp)
                .then(modifier)
                .clip(MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier.clip(MaterialTheme.shapes.medium),
            contentScale = contentScale
        )
    }
}
