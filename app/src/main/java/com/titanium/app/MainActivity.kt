package com.titanium.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.titanium.app.ui.admin.AdminGateScreen
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
 * Solo dos "zonas": la app pública (de cara a los socios) y el área de
 * administración, a la que se llega manteniendo pulsado el nombre de la
 * asociación en la cabecera (ver HeaderCard).
 */
@Composable
private fun TitaniumApp() {
    var showAdminArea by remember { mutableStateOf(false) }
    val session = rememberAdminSession()

    if (showAdminArea) {
        AdminGateScreen(session = session, onExit = { showAdminArea = false })
    } else {
        HomeScreen(onAdminAccessRequested = { showAdminArea = true })
    }
}
