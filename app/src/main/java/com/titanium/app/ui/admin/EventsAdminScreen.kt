package com.titanium.app.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.EventEntry
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.ui.components.RemoteOrPlaceholderImage
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

private data class EditingEvent(
    val id: String = "",
    val day: String = "",
    val month: String = "",
    val bandNames: List<String> = emptyList(),
    val posterUrl: String? = null
) {
    companion object {
        fun blank() = EditingEvent()
        fun from(event: EventEntry) = EditingEvent(
            id = event.id,
            day = event.day,
            month = event.month,
            bandNames = event.bandNames,
            posterUrl = event.posterImageUrl
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    val events by repository.observeEvents().collectAsState(initial = emptyList())
    var editing by remember { mutableStateOf<EditingEvent?>(null) }
    val scope = rememberCoroutineScope()

    fun save(entry: EditingEvent) {
        scope.launch {
            repository.addOrUpdateEvent(
                EventEntry(
                    id = entry.id,
                    day = entry.day,
                    month = entry.month,
                    bandNames = entry.bandNames,
                    posterImageUrl = entry.posterUrl
                )
            )
            editing = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Próximos eventos") })

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Eventos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir evento") {
                editing = EditingEvent.blank()
            }
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            if (editing != null && editing!!.id.isBlank()) {
                item {
                    EventEditCard(
                        editing = editing!!,
                        onChange = { editing = it },
                        onSave = { save(editing!!) },
                        onCancel = { editing = null },
                        onDelete = null
                    )
                }
            }
            items(events, key = { it.id }) { event ->
                if (editing?.id == event.id) {
                    EventEditCard(
                        editing = editing!!,
                        onChange = { editing = it },
                        onSave = { save(editing!!) },
                        onCancel = { editing = null },
                        onDelete = {
                            scope.launch {
                                repository.deleteEvent(event.id)
                                editing = null
                            }
                        }
                    )
                } else {
                    EventViewCard(event = event, onEdit = { editing = EditingEvent.from(event) })
                }
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}

@Composable
private fun EventViewCard(event: EventEntry, onEdit: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AdminDateBadge(day = event.day, month = event.month)
                Text(
                    text = event.bandNames.joinToString(" · "),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                )
                TranslucentIconButton(icon = Icons.Default.Edit, contentDescription = "Editar evento", onClick = onEdit)
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Desplegar",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                RemoteOrPlaceholderImage(
                    imageUrl = event.posterImageUrl,
                    contentDescription = "Cartel del evento",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventEditCard(
    editing: EditingEvent,
    onChange: (EditingEvent) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)?
) {
    var newBandName by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showDatePicker = true }
            ) {
                AdminDateBadge(
                    day = editing.day.ifBlank { "--" },
                    month = editing.month.ifBlank { "MES" }
                )
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
            editing.bandNames.forEachIndexed { index, name ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {
                        onChange(editing.copy(bandNames = editing.bandNames.toMutableList().apply { removeAt(index) }))
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
                        onChange(editing.copy(bandNames = editing.bandNames + newBandName.trim()))
                        newBandName = ""
                    }
                ) { Text("Añadir") }
            }

            Text(text = "Cartel del evento", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            TappableImageUpload(
                imageUrl = editing.posterUrl,
                folder = "events",
                contentDescription = "Cartel del evento",
                onUploaded = { onChange(editing.copy(posterUrl = it)) },
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedButton(
                    enabled = editing.day.isNotBlank() && editing.month.isNotBlank() && editing.bandNames.isNotEmpty(),
                    onClick = onSave
                ) {
                    Text(if (editing.id.isBlank()) "Añadir evento" else "Guardar cambios")
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
                        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                        val month = MONTH_ABBREVIATIONS[calendar.get(Calendar.MONTH)]
                        onChange(editing.copy(day = day, month = month))
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
private fun AdminDateBadge(day: String, month: String) {
    Box(modifier = Modifier.width(56.dp).size(56.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = month.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = day, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
