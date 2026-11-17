package com.titanium.app.util

import android.content.Context
import java.io.IOException

/** Comprueba si un fichero existe dentro de app/src/main/assets/. */
fun assetExists(context: Context, assetPath: String): Boolean {
    return try {
        context.assets.open(assetPath).close()
        true
    } catch (e: IOException) {
        false
    }
}
