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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private enum class GateStep { AUTH, NOT_AUTHORIZED, DASHBOARD }

/**
 * Diálogo de acceso de administrador, por encima de la pantalla
 * principal (que nunca se desmonta mientras tanto, así el vídeo de intro
 * no se repite). Pasa por login → panel de administración (con "ir a la
 * app", gestionar administradores y cerrar sesión) → solo se cierra
 * cuando la persona pulsa explícitamente "ir a la app" o cierra sesión.
 */
@Composable
fun AdminGateDialog(session: AdminSession, onDismiss: () -> Unit, onManageAdminsRequested: () -> Unit) {
    var step by remember(session.isLoggedIn, session.isAdmin) {
        mutableStateOf(
            when {
                !session.isLoggedIn -> GateStep.AUTH
                !session.isAdmin -> GateStep.NOT_AUTHORIZED
                else -> GateStep.DASHBOARD
            }
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (step) {
                GateStep.AUTH -> AdminAuthScreen(session = session, onBack = onDismiss)
                GateStep.NOT_AUTHORIZED -> NotAuthorizedContent(session = session, onExit = onDismiss)
                GateStep.DASHBOARD -> AdminDashboardScreen(
                    session = session,
                    onEnterEditMode = onDismiss,
                    onManageAdminsRequested = onManageAdminsRequested,
                    onLogout = {
                        session.signOut()
                        onDismiss()
                    }
                )
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
