package com.titanium.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Gestiona la lista de administradores autorizados (colección `admins`,
 * un documento por email). Esta lista es lo que de verdad decide quién
 * puede editar contenido: las reglas de seguridad de Firestore
 * (firestore.rules) comprueban esta misma colección antes de aceptar
 * cualquier escritura en eventos/sede/galería/etc.
 */
class AdminRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun docIdFor(email: String) = email.trim().lowercase()

    /** true si ese email está en la lista y activo. */
    fun observeIsAdmin(email: String): Flow<Boolean> = callbackFlow {
        val registration = db.collection("admins").document(docIdFor(email))
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.getBoolean("active") == true)
            }
        awaitClose { registration.remove() }
    }

    /** true si además puede añadir/quitar a otros administradores. */
    fun observeCanManageAdmins(email: String): Flow<Boolean> = callbackFlow {
        val registration = db.collection("admins").document(docIdFor(email))
            .addSnapshotListener { snapshot, _ ->
                trySend(
                    snapshot?.getBoolean("active") == true &&
                        snapshot.getBoolean("canManageAdmins") == true
                )
            }
        awaitClose { registration.remove() }
    }

    fun observeAdmins(): Flow<List<AdminEntry>> = callbackFlow {
        val registration = db.collection("admins")
            .addSnapshotListener { snapshot, _ ->
                val admins = snapshot?.documents?.map { doc ->
                    AdminEntry(
                        email = doc.id,
                        active = doc.getBoolean("active") ?: true,
                        canManageAdmins = doc.getBoolean("canManageAdmins") ?: false,
                        addedBy = doc.getString("addedBy")
                    )
                } ?: emptyList()
                trySend(admins)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Autoriza un nuevo email como administrador (todavía no crea ninguna
     * cuenta: eso lo hace la propia persona registrándose con ese email,
     * ver AuthRepository.signUp).
     */
    suspend fun addAdmin(email: String, addedBy: String, canManageAdmins: Boolean = false) {
        db.collection("admins").document(docIdFor(email))
            .set(
                mapOf(
                    "active" to true,
                    "canManageAdmins" to canManageAdmins,
                    "addedBy" to addedBy
                )
            )
            .await()
    }

    /** Quita permisos sin borrar el historial de quién lo añadió. */
    suspend fun deactivateAdmin(email: String) {
        db.collection("admins").document(docIdFor(email))
            .update("active", false)
            .await()
    }

    suspend fun removeAdmin(email: String) {
        db.collection("admins").document(docIdFor(email)).delete().await()
    }
}
