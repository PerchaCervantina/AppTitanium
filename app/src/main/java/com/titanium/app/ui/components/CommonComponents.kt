package com.titanium.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.titanium.app.R
import com.titanium.app.data.ContentRepository

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
 * Muestra la imagen cuyo nombre de recurso drawable viene en [drawableName].
 * Si ese recurso todavía no existe en el proyecto (porque no se ha añadido
 * la foto real), se muestra un icono comodín para que nada se rompa.
 */
@Composable
fun AssetOrPlaceholderImage(
    drawableName: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resId = remember(drawableName) { ContentRepository.drawableIdOrNull(context, drawableName) }

    Image(
        painter = painterResource(resId ?: R.drawable.ic_placeholder_image),
        contentDescription = contentDescription,
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        contentScale = if (resId != null) contentScale else ContentScale.Inside
    )
}
