package com.titanium.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.EventEntry
import com.titanium.app.ui.admin.MONTH_ABBREVIATIONS
import com.titanium.app.ui.admin.TappableImageUpload
import com.titanium.app.ui.admin.TranslucentIconButton
import java.util.Calendar
import java.util.TimeZone

/**
 * Tarjeta de un evento. En modo público (`isAdmin = false`) solo se puede
 * desplegar para ver el cartel. En modo administrador muestra además un
 * lápiz que la convierte, en el sitio, en un formulario de edición (fecha,
 * grupos, cartel) — así se ve siempre con el mismo aspecto que tendrá en
 * la app, sin tener que ir a otra pantalla y volver para comprobarlo.
 */
@Composable
fun EventCard(
    event: EventEntry,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onSave: (EventEntry) -> Unit = {},
    onDelete: (() -> Unit)? = null
) {
    var editing by remember { mutableStateOf(false) }

    if (editing) {
        EventEditCard(
            initial = event,
            modifier = modifier,
            onSave = { onSave(it); editing = false },
            onCancel = { editing = false },
            onDelete = onDelete?.let { { it(); editing = false } }
        )
        return
    }

    var expanded by remember { mutableStateOf(false) }
    var showZoomedPoster by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EventDateBadge(day = event.day, month = event.month)

                Text(
                    text = event.bandNames.joinToString(" · "),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )

                if (isAdmin) {
                    TranslucentIconButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Editar evento",
                        onClick = { editing = true }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Desplegar",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                // El cartel se muestra completo y opaco (sin fundirse con el
                // fondo ni recortarse): es el cartel real del concierto,
                // compartido por todos los grupos de ese día.
                RemoteOrPlaceholderImage(
                    imageUrl = event.posterImageUrl,
                    contentDescription = "Cartel del evento",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clickable(enabled = !event.posterImageUrl.isNullOrBlank()) { showZoomedPoster = true }
                )
            }
        }
    }

    if (showZoomedPoster) {
        ZoomableImageOverlay(
            imageUrl = event.posterImageUrl,
            contentDescription = "Cartel del evento",
            onDismiss = { showZoomedPoster = false }
        )
    }
}

/** Tarjeta en blanco para crear un evento nuevo (botón "+" de la sección). */
@Composable
fun NewEventCard(modifier: Modifier = Modifier, onSave: (EventEntry) -> Unit, onCancel: () -> Unit) {
    EventEditCard(initial = null, modifier = modifier, onSave = onSave, onCancel = onCancel, onDelete = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventEditCard(
    initial: EventEntry?,
    modifier: Modifier = Modifier,
    onSave: (EventEntry) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?
) {
    var day by remember { mutableStateOf(initial?.day.orEmpty()) }
    var month by remember { mutableStateOf(initial?.month.orEmpty()) }
    var bandNames by remember { mutableStateOf(initial?.bandNames ?: emptyList()) }
    var posterUrl by remember { mutableStateOf(initial?.posterImageUrl) }
    var newBandName by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showDatePicker = true }
            ) {
                EventDateBadge(day = day.ifBlank { "--" }, month = month.ifBlank { "MES" })
                Text(
                    text = "Toca para elegir la fecha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                )
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Elegir fecha",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Text(text = "Grupos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            bandNames.forEachIndexed { index, name ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {
                        bandNames = bandNames.toMutableList().apply { removeAt(index) }
                    }) { Text("Quitar") }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = newBandName,
                    onValueChange = { newBandName = it },
                    label = { Text("Nombre del grupo") },
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    enabled = newBandName.isNotBlank(),
                    onClick = {
                        bandNames = bandNames + newBandName.trim()
                        newBandName = ""
                    }
                ) { Text("Añadir") }
            }

            Text(text = "Cartel del evento", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            TappableImageUpload(
                imageUrl = posterUrl,
                folder = "events",
                contentDescription = "Cartel del evento",
                onUploaded = { posterUrl = it },
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedButton(
                    enabled = day.isNotBlank() && month.isNotBlank() && bandNames.isNotEmpty(),
                    onClick = {
                        onSave(
                            EventEntry(
                                id = initial?.id.orEmpty(),
                                day = day,
                                month = month,
                                bandNames = bandNames,
                                posterImageUrl = posterUrl
                            )
                        )
                    }
                ) {
                    Text(if (initial == null) "Añadir evento" else "Guardar cambios")
                }
                TextButton(onClick = onCancel) { Text("Cancelar") }
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Eliminar evento") }
                }
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.timeInMillis = millis
                        day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                        month = MONTH_ABBREVIATIONS[calendar.get(Calendar.MONTH)]
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun EventDateBadge(day: String, month: String) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = month.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = day,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
