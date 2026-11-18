package com.titanium.app.data.cloudinary

/**
 * Datos de tu cuenta gratuita de Cloudinary (ver CLOUDINARY_SETUP.md).
 * Ninguno de los dos es secreto: están pensados para ir dentro de la app
 * (la subida "sin firmar" de Cloudinary funciona así a propósito). La
 * protección contra abusos se configura en el propio preset, desde el
 * panel de Cloudinary, no con una clave secreta aquí.
 */
object CloudinaryConfig {
    const val CLOUD_NAME = "xngqkzli"
    const val UPLOAD_PRESET = "upload preset"

    val isConfigured: Boolean
        get() = CLOUD_NAME != "PON_AQUI_TU_CLOUD_NAME" && UPLOAD_PRESET != "PON_AQUI_TU_UPLOAD_PRESET"
}
