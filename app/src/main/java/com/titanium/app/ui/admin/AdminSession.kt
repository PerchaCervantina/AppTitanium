package com.titanium.app.ui.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuthException
import com.titanium.app.data.firebase.AdminRepository
import com.titanium.app.data.firebase.AuthRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

/**
 * Sesión de administrador: quién ha iniciado sesión (Firebase Auth) y si
 * ese email está autorizado como administrador (colección `admins`).
 * Centraliza esta comprobación para que todas las pantallas de admin la
 * usen igual.
 */
class AdminSession(
    private val authRepository: AuthRepository,
    private val adminRepository: AdminRepository
) {
    var email: String? by mutableStateOf(authRepository.currentUser?.email)
    var isAdmin: Boolean by mutableStateOf(false)
    var canManageAdmins: Boolean by mutableStateOf(false)
    var errorMessage: String? by mutableStateOf(null)
    var isLoading: Boolean by mutableStateOf(false)

    val isLoggedIn: Boolean get() = email != null

    suspend fun observeForever() {
        authRepository.currentUserFlow().collectLatest { user ->
            email = user?.email
            if (user?.email == null) {
                isAdmin = false
                canManageAdmins = false
            } else {
                combine(
                    adminRepository.observeIsAdmin(user.email!!),
                    adminRepository.observeCanManageAdmins(user.email!!)
                ) { admin, canManage -> admin to canManage }
                    .collectLatest { (admin, canManage) ->
                        isAdmin = admin
                        canManageAdmins = canManage
                    }
            }
        }
    }

    suspend fun signIn(email: String, password: String) {
        errorMessage = null
        isLoading = true
        try {
            authRepository.signIn(email, password)
        } catch (e: FirebaseAuthException) {
            errorMessage = describeAuthError(e)
        } finally {
            isLoading = false
        }
    }

    suspend fun signUp(email: String, password: String) {
        errorMessage = null
        isLoading = true
        try {
            authRepository.signUp(email, password)
        } catch (e: FirebaseAuthException) {
            errorMessage = describeAuthError(e)
        } finally {
            isLoading = false
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    private fun describeAuthError(e: FirebaseAuthException): String = when (e.errorCode) {
        "ERROR_INVALID_EMAIL" -> "El email no es válido."
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Contraseña incorrecta."
        "ERROR_USER_NOT_FOUND" -> "No existe ninguna cuenta con ese email. Si es la primera vez, regístrate."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con ese email. Inicia sesión en vez de registrarte."
        "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado corta (mínimo 6 caracteres)."
        else -> e.message ?: "No se ha podido completar la operación."
    }
}

@Composable
fun rememberAdminSession(
    authRepository: AuthRepository = remember { AuthRepository() },
    adminRepository: AdminRepository = remember { AdminRepository() }
): AdminSession {
    val session = remember { AdminSession(authRepository, adminRepository) }
    LaunchedEffect(session) { session.observeForever() }
    return session
}
