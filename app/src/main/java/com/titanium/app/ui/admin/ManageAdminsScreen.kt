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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.AdminEntry
import com.titanium.app.data.firebase.AdminRepository
import kotlinx.coroutines.launch

/**
 * Solo accesible para quien tenga `canManageAdmins = true`. Permite dar de
 * alta un nuevo email autorizado, y activar/desactivar o quitar a los
 * existentes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAdminsScreen(
    session: AdminSession,
    adminRepository: AdminRepository = remember { AdminRepository() },
    onBack: () -> Unit
) {
    val admins by adminRepository.observeAdmins().collectAsState(initial = emptyList())
    var newEmail by remember { mutableStateOf("") }
    var newCanManage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Gestionar administradores") })

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Añadir administrador",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("Email de la persona") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Checkbox(checked = newCanManage, onCheckedChange = { newCanManage = it })
                Text("También puede gestionar administradores")
            }
            TextButton(
                enabled = newEmail.isNotBlank(),
                onClick = {
                    val emailToAdd = newEmail
                    scope.launch {
                        adminRepository.addAdmin(
                            email = emailToAdd,
                            addedBy = session.email.orEmpty(),
                            canManageAdmins = newCanManage
                        )
                        newEmail = ""
                        newCanManage = false
                    }
                }
            ) {
                Text("Autorizar este email como administrador")
            }

            Text(
                text = "La persona deberá registrarse en la app con este mismo email para poder entrar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(admins) { admin ->
                AdminRow(admin = admin, adminRepository = adminRepository)
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
            Text("Volver")
        }
    }
}

@Composable
private fun AdminRow(admin: AdminEntry, adminRepository: AdminRepository) {
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = admin.email, style = MaterialTheme.typography.titleMedium)
            Text(
                text = buildString {
                    append(if (admin.active) "Activo" else "Inactivo")
                    if (admin.canManageAdmins) append(" · puede gestionar administradores")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = {
                    scope.launch {
                        if (admin.active) adminRepository.deactivateAdmin(admin.email)
                        else adminRepository.addAdmin(admin.email, admin.addedBy.orEmpty(), admin.canManageAdmins)
                    }
                }) {
                    Text(if (admin.active) "Quitar permisos" else "Reactivar")
                }
                TextButton(onClick = { scope.launch { adminRepository.removeAdmin(admin.email) } }) {
                    Text("Eliminar")
                }
            }
        }
    }
}
