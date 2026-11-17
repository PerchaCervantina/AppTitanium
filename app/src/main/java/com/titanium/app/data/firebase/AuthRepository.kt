package com.titanium.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Inicio/cierre de sesión de administradores con Firebase Auth
 * (email + contraseña). No hay "roles" aquí: solo identifica quién es la
 * persona. Que tenga permisos de administrador lo decide AdminRepository,
 * mirando si su email está en la lista de autorizados.
 */
class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun currentUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(normalizeEmail(email), password).await()
    }

    /**
     * Crea la cuenta de una persona que ya ha sido añadida a la lista de
     * administradores autorizados con ese mismo email (ver AdminRepository).
     * Crear la cuenta por sí sola no da ningún permiso: solo identifica a
     * la persona.
     */
    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(normalizeEmail(email), password).await()
    }

    // Siempre en minúsculas: así coincide exactamente con el id usado en
    // la colección `admins` (ver AdminRepository.docIdFor) y las reglas de
    // seguridad de Firestore pueden comparar el email tal cual, sin
    // depender de funciones de minúsculas en el propio lenguaje de reglas.
    private fun normalizeEmail(email: String) = email.trim().lowercase()

    fun signOut() {
        auth.signOut()
    }
}
