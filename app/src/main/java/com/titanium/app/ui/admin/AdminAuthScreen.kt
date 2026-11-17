package com.titanium.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de acceso de administradores. No es pública: solo se llega
 * aquí desde un punto de entrada escondido (ver HomeScreen). Registrarse
 * aquí no da ningún permiso por sí solo: solo funciona si ese email ya ha
 * sido autorizado antes por otro administrador (ver CONTENIDO.md).
 */
@Composable
fun AdminAuthScreen(session: AdminSession, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUpMode) "Registro de administrador" else "Acceso de administrador",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isSignUpMode) {
                "Usa el mismo email que te haya dado de alta otro administrador."
            } else {
                "Inicia sesión con tu email y contraseña de administrador."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        if (session.errorMessage != null) {
            Text(
                text = session.errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            enabled = !session.isLoading && email.isNotBlank() && password.isNotBlank(),
            onClick = {
                scope.launch {
                    if (isSignUpMode) session.signUp(email, password) else session.signIn(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(if (isSignUpMode) "Crear cuenta" else "Entrar")
        }

        TextButton(onClick = { isSignUpMode = !isSignUpMode }, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (isSignUpMode) "Ya tengo cuenta, quiero iniciar sesión"
                else "Es la primera vez, quiero registrarme"
            )
        }

        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a la app")
        }
    }
}
