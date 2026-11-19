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
    val description: String = "",
    val location: String = ""
)

data class MembershipInfo(
    val intro: String = "",
    // Enlaces a redes sociales y contacto. Cada botón del "Hazte socio"
    // solo aparece si su campo correspondiente no está vacío.
    val instagramUrl: String = "",
    val facebookUrl: String = "",
    val tiktokUrl: String = "",
    val email: String = ""
)

/**
 * Un evento es una fecha con uno o varios grupos actuando ese día, y UN
 * único cartel para todo el evento (el cartel real del concierto, no un
 * logo por grupo). No hay descripción ni enlace.
 */
data class EventEntry(
    val id: String = "",
    val day: String = "",
    val month: String = "",
    val bandNames: List<String> = emptyList(),
    val posterImageUrl: String? = null,
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
