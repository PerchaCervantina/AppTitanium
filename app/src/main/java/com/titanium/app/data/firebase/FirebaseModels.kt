package com.titanium.app.data.firebase

/**
 * Modelos que se leen/escriben en Firestore. A diferencia de los modelos
 * antiguos (ContentModels.kt, leídos de assets/content.json), las imágenes
 * aquí son URLs de Firebase Storage, no nombres de recursos drawable.
 */

data class AdminEntry(
    val email: String,
    val active: Boolean = true,
    val canManageAdmins: Boolean = false,
    val addedBy: String? = null
)

data class PresentationInfo(
    val name: String = "Titanium",
    val description: String = "",
    val location: String = ""
)

data class MembershipInfo(
    val intro: String = "",
    val whatsappNumber: String = "",
    val whatsappMessage: String = ""
)

data class EventEntry(
    val id: String = "",
    val day: String = "",
    val month: String = "",
    val bandName: String = "",
    val musicDescription: String = "",
    val logoImageUrl: String? = null,
    val socialUrl: String? = null,
    val createdAt: Long = 0L
)

data class VenuePhoto(
    val id: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val createdAt: Long = 0L
)

data class GalleryPhoto(
    val id: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val createdAt: Long = 0L
)

/**
 * Vídeo de la galería SIN alojar el fichero: solo un enlace a la
 * publicación ya subida a Instagram/Facebook, para no generar coste de
 * almacenamiento ni de ancho de banda en Firebase.
 */
data class GalleryVideoLink(
    val id: String = "",
    val linkUrl: String = "",
    val caption: String = "",
    val thumbnailImageUrl: String? = null,
    val createdAt: Long = 0L
)
