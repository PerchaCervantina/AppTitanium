package com.titanium.app.data

import kotlinx.serialization.Serializable

/**
 * Estructura completa de contenidos de la app, leída desde
 * `assets/content.json`. Cualquier persona que no sea programadora puede
 * editar ese fichero con un editor de texto para cambiar los contenidos
 * de la app (ver CONTENIDO.md en la raíz del proyecto).
 */
@Serializable
data class AppContent(
    val intro: IntroContent,
    val presentation: PresentationContent,
    val events: List<EventItem> = emptyList(),
    val venue: VenueContent,
    val gallery: GalleryContent,
    val membership: MembershipContent
)

@Serializable
data class IntroContent(
    // Nombre del vídeo dentro de app/src/main/assets/videos/, por ejemplo "intro.mp4".
    // Si el fichero no existe, la app pasa directamente a la pantalla principal.
    val videoAsset: String
)

@Serializable
data class PresentationContent(
    val name: String,
    val description: String,
    val location: String
)

@Serializable
data class EventItem(
    val id: String,
    // Día del mes, ej. "14"
    val day: String,
    // Mes abreviado, ej. "DIC"
    val month: String,
    val bandName: String,
    val musicDescription: String,
    // Nombre del recurso drawable con el logo del grupo (sin extensión), ej. "logo_nombre_grupo".
    val logoDrawable: String? = null,
    // Enlace opcional a redes sociales del grupo.
    val socialUrl: String? = null
)

@Serializable
data class VenueContent(
    val images: List<CaptionedImage> = emptyList()
)

@Serializable
data class CaptionedImage(
    val drawable: String,
    val caption: String
)

@Serializable
data class GalleryContent(
    val photos: List<GalleryPhoto> = emptyList(),
    val videos: List<GalleryVideo> = emptyList()
)

@Serializable
data class GalleryPhoto(
    val drawable: String,
    val caption: String
)

@Serializable
data class GalleryVideo(
    // Nombre del vídeo dentro de app/src/main/assets/videos/, ej. "concierto_marzo.mp4".
    val videoAsset: String,
    // Drawable usado como miniatura mientras no se reproduce el vídeo.
    val thumbnailDrawable: String? = null,
    val caption: String
)

@Serializable
data class MembershipContent(
    val intro: String,
    // Número de WhatsApp en formato internacional sin "+" ni espacios, ej. "34600000000".
    val whatsappNumber: String,
    val whatsappMessage: String
)
