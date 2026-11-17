package com.titanium.app.data

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Carga [AppContent] desde `assets/content.json`.
 */
object ContentRepository {

    private val json = Json { ignoreUnknownKeys = true }

    fun load(context: Context): AppContent {
        val text = context.assets.open("content.json").bufferedReader().use { it.readText() }
        return json.decodeFromString(text)
    }

    /**
     * Comprueba si un vídeo añadido en assets/videos/ existe de verdad,
     * para poder omitir el vídeo de intro con elegancia si todavía no se ha
     * incorporado al proyecto.
     */
    fun assetExists(context: Context, assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).close()
            true
        } catch (e: IOException) {
            false
        }
    }

    /** Devuelve el id de un recurso drawable por nombre, o null si no existe. */
    fun drawableIdOrNull(context: Context, name: String?): Int? {
        if (name.isNullOrBlank()) return null
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (id != 0) id else null
    }
}
