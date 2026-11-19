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

    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var membershipIntro by remember { mutableStateOf("") }
    var instagramUrl by remember { mutableStateOf("") }
    var facebookUrl by remember { mutableStateOf("") }
    var tiktokUrl by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    // Solo rellenamos los campos una vez con lo que haya en Firestore, para
    // no pisar lo que la persona esté escribiendo cada vez que llega una
    // actualización en tiempo real.
    LaunchedEffect(presentation, membership) {
        if (!initialized) {
            description = presentation.description
            location = presentation.location
            membershipIntro = membership.intro
            instagramUrl = membership.instagramUrl
            facebookUrl = membership.facebookUrl
            tiktokUrl = membership.tiktokUrl
            contactEmail = membership.email
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
            Text(
                text = "El nombre de la asociación se muestra con el logo y no se edita aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
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
                supportingText = { Text("Se usa tal cual para abrir Google Maps: escribe la dirección real de la sede.") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedButton(
                onClick = { scope.launch { repository.updatePresentation(PresentationInfo(description, location)) } },
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            ) { Text("Guardar presentación") }

            HorizontalDivider()

            Text(text = "Hazte socio", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            Text(
                text = "Deja vacío cualquiera de estos campos para que su botón no aparezca en la app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            OutlinedTextField(
                value = membershipIntro,
                onValueChange = { membershipIntro = it },
                label = { Text("Texto de invitación") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = instagramUrl,
                onValueChange = { instagramUrl = it },
                label = { Text("Enlace de Instagram (perfil o chat)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = facebookUrl,
                onValueChange = { facebookUrl = it },
                label = { Text("Enlace de Facebook") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = tiktokUrl,
                onValueChange = { tiktokUrl = it },
                label = { Text("Enlace de TikTok") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Correo electrónico de contacto") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedButton(
                onClick = {
                    scope.launch {
                        repository.updateMembership(
                            MembershipInfo(membershipIntro, instagramUrl, facebookUrl, tiktokUrl, contactEmail)
                        )
                    }
                },
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("Guardar contacto") }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) { Text("Volver") }
    }
}
