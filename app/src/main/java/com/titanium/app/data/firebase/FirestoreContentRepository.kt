package com.titanium.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Lee y escribe todo el contenido "público" de la app (antes vivía en
 * assets/content.json, ahora en Firestore). La subida de imágenes no pasa
 * por aquí: la hace CloudinaryRepository (ver ese fichero para el porqué).
 *
 * Las lecturas no requieren haber iniciado sesión (son públicas); las
 * escrituras solo las aceptan las reglas de seguridad si el email de quien
 * las hace está en la colección `admins` como activo (ver
 * firestore.rules). Esta clase no decide permisos, solo llama a Firebase.
 */
class FirestoreContentRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ---- Presentación y contacto (documentos únicos) ----

    fun observePresentation(): Flow<PresentationInfo> = callbackFlow {
        val registration = db.collection("config").document("presentation")
            .addSnapshotListener { snapshot, _ ->
                trySend(
                    PresentationInfo(
                        description = snapshot?.getString("description") ?: "",
                        location = snapshot?.getString("location") ?: ""
                    )
                )
            }
        awaitClose { registration.remove() }
    }

    suspend fun updatePresentation(info: PresentationInfo) {
        db.collection("config").document("presentation")
            .set(mapOf("description" to info.description, "location" to info.location))
            .await()
    }

    fun observeMembership(): Flow<MembershipInfo> = callbackFlow {
        val registration = db.collection("config").document("membership")
            .addSnapshotListener { snapshot, _ ->
                trySend(
                    MembershipInfo(
                        intro = snapshot?.getString("intro") ?: "",
                        instagramUrl = snapshot?.getString("instagramUrl") ?: "",
                        facebookUrl = snapshot?.getString("facebookUrl") ?: "",
                        tiktokUrl = snapshot?.getString("tiktokUrl") ?: "",
                        email = snapshot?.getString("email") ?: ""
                    )
                )
            }
        awaitClose { registration.remove() }
    }

    suspend fun updateMembership(info: MembershipInfo) {
        db.collection("config").document("membership")
            .set(
                mapOf(
                    "intro" to info.intro,
                    "instagramUrl" to info.instagramUrl,
                    "facebookUrl" to info.facebookUrl,
                    "tiktokUrl" to info.tiktokUrl,
                    "email" to info.email
                )
            )
            .await()
    }

    // ---- Próximos eventos ----

    fun observeEvents(): Flow<List<EventEntry>> = callbackFlow {
        val registration = db.collection("events")
            .addSnapshotListener { snapshot, _ ->
                val events = snapshot?.documents?.map { it.toEvent() } ?: emptyList()
                trySend(events)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addOrUpdateEvent(event: EventEntry) {
        val id = event.id.ifBlank { UUID.randomUUID().toString() }
        db.collection("events").document(id)
            .set(event.copy(id = id, createdAt = event.createdAt.takeIf { it > 0 } ?: System.currentTimeMillis()))
            .await()
    }

    suspend fun deleteEvent(id: String) {
        db.collection("events").document(id).delete().await()
    }

    // ---- Nuestra sede ----

    fun observeVenuePhotos(): Flow<List<VenuePhoto>> = callbackFlow {
        val registration = db.collection("venuePhotos")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.map { it.toVenuePhoto() } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    suspend fun addVenuePhoto(imageUrl: String, caption: String) {
        val id = UUID.randomUUID().toString()
        db.collection("venuePhotos").document(id)
            .set(VenuePhoto(id = id, imageUrl = imageUrl, caption = caption, createdAt = System.currentTimeMillis()))
            .await()
    }

    suspend fun deleteVenuePhoto(id: String) {
        db.collection("venuePhotos").document(id).delete().await()
    }

    // ---- Galería: fotos ----

    fun observeGalleryPhotos(): Flow<List<GalleryPhoto>> = callbackFlow {
        val registration = db.collection("galleryPhotos")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.map { it.toGalleryPhoto() } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    suspend fun addGalleryPhoto(imageUrl: String, caption: String) {
        val id = UUID.randomUUID().toString()
        db.collection("galleryPhotos").document(id)
            .set(GalleryPhoto(id = id, imageUrl = imageUrl, caption = caption, createdAt = System.currentTimeMillis()))
            .await()
    }

    suspend fun deleteGalleryPhoto(id: String) {
        db.collection("galleryPhotos").document(id).delete().await()
    }

    // ---- Galería: vídeos (enlaces a Instagram/Facebook, sin alojar el fichero) ----

    fun observeGalleryVideoLinks(): Flow<List<GalleryVideoLink>> = callbackFlow {
        val registration = db.collection("galleryVideoLinks")
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.documents?.map { it.toGalleryVideoLink() } ?: emptyList())
            }
        awaitClose { registration.remove() }
    }

    suspend fun addGalleryVideoLink(linkUrl: String, caption: String, thumbnailImageUrl: String?) {
        val id = UUID.randomUUID().toString()
        db.collection("galleryVideoLinks").document(id)
            .set(
                GalleryVideoLink(
                    id = id,
                    linkUrl = linkUrl,
                    caption = caption,
                    thumbnailImageUrl = thumbnailImageUrl,
                    createdAt = System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun deleteGalleryVideoLink(id: String) {
        db.collection("galleryVideoLinks").document(id).delete().await()
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toEvent() = EventEntry(
    id = id,
    day = getString("day") ?: "",
    month = getString("month") ?: "",
    bandName = getString("bandName") ?: "",
    musicDescription = getString("musicDescription") ?: "",
    logoImageUrl = getString("logoImageUrl"),
    socialUrl = getString("socialUrl"),
    createdAt = getLong("createdAt") ?: 0L
)

private fun com.google.firebase.firestore.DocumentSnapshot.toVenuePhoto() = VenuePhoto(
    id = id,
    imageUrl = getString("imageUrl") ?: "",
    caption = getString("caption") ?: "",
    createdAt = getLong("createdAt") ?: 0L
)

private fun com.google.firebase.firestore.DocumentSnapshot.toGalleryPhoto() = GalleryPhoto(
    id = id,
    imageUrl = getString("imageUrl") ?: "",
    caption = getString("caption") ?: "",
    createdAt = getLong("createdAt") ?: 0L
)

private fun com.google.firebase.firestore.DocumentSnapshot.toGalleryVideoLink() = GalleryVideoLink(
    id = id,
    linkUrl = getString("linkUrl") ?: "",
    caption = getString("caption") ?: "",
    thumbnailImageUrl = getString("thumbnailImageUrl"),
    createdAt = getLong("createdAt") ?: 0L
)
