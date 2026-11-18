package com.titanium.app.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class AdminScreenState {
    AUTH, NOT_AUTHORIZED, DASHBOARD, EVENTS, VENUE, GALLERY, PRESENTATION, MANAGE_ADMINS
}

/**
 * Punto de entrada del área de administración: decide qué pantalla
 * mostrar según si hay sesión iniciada y si ese email está autorizado.
 */
@Composable
fun AdminGateScreen(session: AdminSession, onExit: () -> Unit) {
    var screen by remember(session.isLoggedIn, session.isAdmin) {
        mutableStateOf(
            when {
                !session.isLoggedIn -> AdminScreenState.AUTH
                !session.isAdmin -> AdminScreenState.NOT_AUTHORIZED
                else -> AdminScreenState.DASHBOARD
            }
        )
    }

    // El botón atrás del sistema debe navegar dentro del panel (a la
    // pantalla anterior), no cerrar la app directamente: sin esto, Android
    // trata cada pantalla de admin como si fuera la pantalla principal.
    BackHandler(enabled = true) {
        val isSubScreen = screen !in setOf(
            AdminScreenState.DASHBOARD, AdminScreenState.AUTH, AdminScreenState.NOT_AUTHORIZED
        )
        if (isSubScreen) {
            screen = AdminScreenState.DASHBOARD
        } else {
            onExit()
        }
    }

    when (screen) {
        AdminScreenState.AUTH -> AdminAuthScreen(session = session, onBack = onExit)
        AdminScreenState.NOT_AUTHORIZED -> NotAuthorizedScreen(session = session, onExit = onExit)
        AdminScreenState.DASHBOARD -> AdminDashboardScreen(
            session = session,
            onNavigate = { destination ->
                screen = when (destination) {
                    AdminDestination.EVENTS -> AdminScreenState.EVENTS
                    AdminDestination.VENUE -> AdminScreenState.VENUE
                    AdminDestination.GALLERY -> AdminScreenState.GALLERY
                    AdminDestination.PRESENTATION -> AdminScreenState.PRESENTATION
                    AdminDestination.MANAGE_ADMINS -> AdminScreenState.MANAGE_ADMINS
                }
            },
            onBackToApp = onExit
        )
        AdminScreenState.EVENTS -> EventsAdminScreen(onBack = { screen = AdminScreenState.DASHBOARD })
        AdminScreenState.VENUE -> VenueAdminScreen(onBack = { screen = AdminScreenState.DASHBOARD })
        AdminScreenState.GALLERY -> GalleryAdminScreen(onBack = { screen = AdminScreenState.DASHBOARD })
        AdminScreenState.PRESENTATION -> PresentationAdminScreen(onBack = { screen = AdminScreenState.DASHBOARD })
        AdminScreenState.MANAGE_ADMINS -> ManageAdminsScreen(session = session, onBack = { screen = AdminScreenState.DASHBOARD })
    }
}

@Composable
private fun NotAuthorizedScreen(session: AdminSession, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tu cuenta no tiene permisos de administrador",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Pide a otro administrador que autorice tu email (${session.email}) " +
                "desde \"Gestionar administradores\".",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )
        OutlinedButton(onClick = {
            session.signOut()
            onExit()
        }) {
            Text("Volver a la app")
        }
    }
}
