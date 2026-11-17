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

enum class AdminDestination {
    EVENTS, VENUE, GALLERY, PRESENTATION, MANAGE_ADMINS
}

@Composable
fun AdminDashboardScreen(
    session: AdminSession,
    onNavigate: (AdminDestination) -> Unit,
    onBackToApp: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Panel de administración",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Conectado como ${session.email}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        DashboardButton("Próximos eventos") { onNavigate(AdminDestination.EVENTS) }
        DashboardButton("Nuestra sede") { onNavigate(AdminDestination.VENUE) }
        DashboardButton("Galería") { onNavigate(AdminDestination.GALLERY) }
        DashboardButton("Presentación y contacto") { onNavigate(AdminDestination.PRESENTATION) }

        if (session.canManageAdmins) {
            DashboardButton("Gestionar administradores") { onNavigate(AdminDestination.MANAGE_ADMINS) }
        }

        OutlinedButton(
            onClick = {
                session.signOut()
                onBackToApp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(text)
    }
}
