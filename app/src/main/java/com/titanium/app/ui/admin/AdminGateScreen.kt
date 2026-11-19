package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Diálogo de acceso de administrador (login o "no autorizada"), por
 * encima de la pantalla principal, que nunca se desmonta mientras tanto
 * (así el vídeo de intro no se repite al entrar/salir de aquí).
 * Se cierra solo en cuanto la sesión pasa a ser de administrador.
 */
@Composable
fun AdminGateDialog(session: AdminSession, onDismiss: () -> Unit) {
    LaunchedEffect(session.isAdmin) {
        if (session.isAdmin) onDismiss()
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (!session.isLoggedIn) {
                AdminAuthScreen(session = session, onBack = onDismiss)
            } else {
                NotAuthorizedContent(session = session, onExit = onDismiss)
            }
        }
    }
}

@Composable
private fun NotAuthorizedContent(session: AdminSession, onExit: () -> Unit) {
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
