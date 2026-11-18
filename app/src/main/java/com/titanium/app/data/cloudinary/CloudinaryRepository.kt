package com.titanium.app.data.cloudinary

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Sube imágenes a Cloudinary mediante una subida "sin firmar" (unsigned
 * upload): no hace falta backend ni clave secreta, solo el nombre de la
 * cuenta (cloud name) y el nombre del preset, ambos pensados para ir
 * dentro de la app (ver CloudinaryConfig). Se eligió Cloudinary en vez de
 * Firebase Storage porque este último exige el plan de pago Blaze incluso
 * para el nivel gratuito, y Cloudinary no requiere tarjeta.
 */
class CloudinaryRepository(private val client: OkHttpClient = OkHttpClient()) {

    suspend fun uploadImage(context: Context, localUri: Uri, folder: String? = null): String = withContext(Dispatchers.IO) {
        check(CloudinaryConfig.isConfigured) {
            "Cloudinary no está configurado todavía. Ver CLOUDINARY_SETUP.md."
        }

        val bytes = context.contentResolver.openInputStream(localUri)?.use { it.readBytes() }
            ?: throw IOException("No se ha podido leer la imagen seleccionada.")

        val bodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", CloudinaryConfig.UPLOAD_PRESET)
            .addFormDataPart("file", "image.jpg", bytes.toRequestBody("image/*".toMediaType()))
        if (!folder.isNullOrBlank()) {
            bodyBuilder.addFormDataPart("folder", folder)
        }
        val requestBody = bodyBuilder.build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/${CloudinaryConfig.CLOUD_NAME}/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val bodyText = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Cloudinary ha rechazado la subida: $bodyText")
            }
            JSONObject(bodyText).getString("secure_url")
        }
    }
}
