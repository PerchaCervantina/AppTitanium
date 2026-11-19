package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Panel al que se llega justo después de iniciar sesión como
 * administrador. Desde aquí se entra a la app para editar contenido
 * (ahora en el sitio, con los botones +/lápiz/− sobre cada sección), se
 * gestionan los administradores, o se cierra sesión.
 */
@Composable
fun AdminDashboardScreen(
    session: AdminSession,
    onEnterEditMode: () -> Unit,
    onManageAdminsRequested: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Panel de administración",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Conectada como ${session.email}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Button(onClick = onEnterEditMode, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("Ir a la app para editar contenido")
        }

        if (session.canManageAdmins) {
            OutlinedButton(onClick = onManageAdminsRequested, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Text("Gestionar administradores")
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}
