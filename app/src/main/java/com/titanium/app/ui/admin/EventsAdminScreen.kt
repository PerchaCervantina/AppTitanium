package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.titanium.app.data.firebase.EventBand
import com.titanium.app.data.firebase.EventEntry
import com.titanium.app.data.firebase.FirestoreContentRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    val events by repository.observeEvents().collectAsState(initial = emptyList())

    var editingId by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var bands by remember { mutableStateOf(listOf<EventBand>()) }
    var newBandName by remember { mutableStateOf("") }
    var newBandPosterUrl by remember { mutableStateOf<String?>(null) }

    fun resetForm() {
        editingId = ""
        day = ""
        month = ""
        bands = emptyList()
        newBandName = ""
        newBandPosterUrl = null
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Próximos eventos") })

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = if (editingId.isBlank()) "Nuevo evento" else "Editando evento",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = day,
                            onValueChange = { day = it },
                            label = { Text("Día") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = month,
                            onValueChange = { month = it },
                            label = { Text("Mes") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Grupos de este día",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    if (bands.isEmpty()) {
                        Text(
                            text = "Todavía no has añadido ningún grupo a este evento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    bands.forEachIndexed { index, band ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text(
                                text = band.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { bands = bands.toMutableList().apply { removeAt(index) } }) {
                                Text("Quitar")
                            }
                        }
                    }

                    Text(
                        text = "Añadir grupo",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    OutlinedTextField(
                        value = newBandName,
                        onValueChange = { newBandName = it },
                        label = { Text("Nombre del grupo") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    ImageUploadField(
                        folder = "events",
                        label = "Cartel del concierto",
                        imageUrl = newBandPosterUrl,
                        onUploaded = { newBandPosterUrl = it }
                    )
                    OutlinedButton(
                        enabled = newBandName.isNotBlank(),
                        onClick = {
                            bands = bands + EventBand(name = newBandName, posterImageUrl = newBandPosterUrl)
                            newBandName = ""
                            newBandPosterUrl = null
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) { Text("Añadir grupo a este evento") }

                    Row(modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)) {
                        OutlinedButton(
                            enabled = day.isNotBlank() && month.isNotBlank() && bands.isNotEmpty(),
                            onClick = {
                                val entry = EventEntry(id = editingId, day = day, month = month, bands = bands)
                                scope.launch {
                                    repository.addOrUpdateEvent(entry)
                                    resetForm()
                                }
                            }
                        ) {
                            Text(if (editingId.isBlank()) "Añadir evento" else "Guardar cambios")
                        }
                        if (editingId.isNotBlank()) {
                            TextButton(onClick = { resetForm() }) { Text("Cancelar edición") }
                        }
                    }
                    HorizontalDivider()
                }
            }

            items(events, key = { it.id }) { event ->
                EventRow(
                    event = event,
                    onEdit = {
                        editingId = event.id
                        day = event.day
                        month = event.month
                        bands = event.bands
                        newBandName = ""
                        newBandPosterUrl = null
                    },
                    onDelete = { scope.launch { repository.deleteEvent(event.id) } }
                )
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}

@Composable
private fun EventRow(event: EventEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${event.day} ${event.month} — ${event.bands.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.titleMedium
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}
