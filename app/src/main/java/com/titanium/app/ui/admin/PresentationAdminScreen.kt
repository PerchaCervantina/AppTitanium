package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.MembershipInfo
import com.titanium.app.data.firebase.PresentationInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationAdminScreen(
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() },
    onBack: () -> Unit
) {
    val presentation by repository.observePresentation().collectAsState(initial = PresentationInfo())
    val membership by repository.observeMembership().collectAsState(initial = MembershipInfo())

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var membershipIntro by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var whatsappMessage by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    // Solo rellenamos los campos una vez con lo que haya en Firestore, para
    // no pisar lo que la persona esté escribiendo cada vez que llega una
    // actualización en tiempo real.
    LaunchedEffect(presentation, membership) {
        if (!initialized) {
            name = presentation.name
            description = presentation.description
            location = presentation.location
            membershipIntro = membership.intro
            whatsappNumber = membership.whatsappNumber
            whatsappMessage = membership.whatsappMessage
            initialized = true
        }
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Presentación y contacto") })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(text = "Presentación", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la asociación") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Texto de presentación") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Localización") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedButton(
                onClick = { scope.launch { repository.updatePresentation(PresentationInfo(name, description, location)) } },
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            ) { Text("Guardar presentación") }

            HorizontalDivider()

            Text(text = "Hazte socio (WhatsApp)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = membershipIntro,
                onValueChange = { membershipIntro = it },
                label = { Text("Texto de invitación") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = whatsappNumber,
                onValueChange = { whatsappNumber = it },
                label = { Text("Número de WhatsApp (sin + ni espacios)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = whatsappMessage,
                onValueChange = { whatsappMessage = it },
                label = { Text("Mensaje predefinido") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedButton(
                onClick = {
                    scope.launch {
                        repository.updateMembership(MembershipInfo(membershipIntro, whatsappNumber, whatsappMessage))
                    }
                },
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("Guardar contacto") }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}
