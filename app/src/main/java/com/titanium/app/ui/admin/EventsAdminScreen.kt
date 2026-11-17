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
import androidx.compose.material3.ExperimentalMaterial3Api
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
    var editing by remember { mutableStateOf(EventEntry()) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Próximos eventos") })

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            item { EventForm(editing, onChange = { editing = it }) }
            item {
                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    OutlinedButton(onClick = {
                        val toSave = editing
                        scope.launch {
                            repository.addOrUpdateEvent(toSave)
                            editing = EventEntry()
                        }
                    }) {
                        Text(if (editing.id.isBlank()) "Añadir evento" else "Guardar cambios")
                    }
                    if (editing.id.isNotBlank()) {
                        TextButton(onClick = { editing = EventEntry() }) { Text("Cancelar edición") }
                    }
                }
            }
            item { HorizontalDivider() }
            items(events) { event ->
                EventRow(
                    event = event,
                    onEdit = { editing = event },
                    onDelete = { scope.launch { repository.deleteEvent(event.id) } }
                )
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}

@Composable
private fun EventForm(event: EventEntry, onChange: (EventEntry) -> Unit) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = event.day,
                onValueChange = { onChange(event.copy(day = it)) },
                label = { Text("Día") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = event.month,
                onValueChange = { onChange(event.copy(month = it)) },
                label = { Text("Mes") },
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = event.bandName,
            onValueChange = { onChange(event.copy(bandName = it)) },
            label = { Text("Nombre del grupo") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = event.musicDescription,
            onValueChange = { onChange(event.copy(musicDescription = it)) },
            label = { Text("Descripción de su música") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = event.socialUrl.orEmpty(),
            onValueChange = { onChange(event.copy(socialUrl = it)) },
            label = { Text("Enlace a redes sociales (opcional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        ImageUploadField(
            folder = "events",
            label = "Logo del grupo",
            imageUrl = event.logoImageUrl,
            onUploaded = { url -> onChange(event.copy(logoImageUrl = url)) }
        )
    }
}

@Composable
private fun EventRow(event: EventEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "${event.day} ${event.month} — ${event.bandName}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = event.musicDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete) { Text("Eliminar") }
            }
        }
    }
}
