package com.titanium.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.titanium.app.data.AppContent
import com.titanium.app.data.ContentRepository
import com.titanium.app.ui.screens.HomeScreen
import com.titanium.app.ui.theme.TitaniumTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val content: AppContent = ContentRepository.load(applicationContext)

        setContent {
            TitaniumTheme {
                HomeScreen(content = content)
            }
        }
    }
}
