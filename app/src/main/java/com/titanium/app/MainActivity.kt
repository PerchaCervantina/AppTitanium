package com.titanium.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.titanium.app.ui.admin.AdminGateDialog
import com.titanium.app.ui.admin.ManageAdminsScreen
import com.titanium.app.ui.admin.rememberAdminSession
import com.titanium.app.ui.screens.HomeScreen
import com.titanium.app.ui.theme.TitaniumTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TitaniumTheme {
                TitaniumApp()
            }
        }
    }
}

/**
 * HomeScreen es la única pantalla: nunca se desmonta mientras la app está
 * abierta (así el vídeo de intro no se repite al entrar/salir del modo
 * administrador). El login y la gestión de administradores se muestran
 * como diálogos por encima, sin sustituirla.
 */
@Composable
private fun TitaniumApp() {
    val session = rememberAdminSession()
    var showAuthDialog by remember { mutableStateOf(false) }
    var showManageAdmins by remember { mutableStateOf(false) }

    HomeScreen(
        session = session,
        onAdminAccessRequested = { showAuthDialog = true },
        onManageAdminsRequested = { showManageAdmins = true }
    )

    if (showAuthDialog) {
        AdminGateDialog(
            session = session,
            onDismiss = { showAuthDialog = false },
            onManageAdminsRequested = { showManageAdmins = true }
        )
    }

    if (showManageAdmins) {
        Dialog(
            onDismissRequest = { showManageAdmins = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                ManageAdminsScreen(session = session, onBack = { showManageAdmins = false })
            }
        }
    }
}
