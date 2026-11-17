# Titanium — App Android de la asociación

App nativa Android (Kotlin + Jetpack Compose) para la asociación cultural
de punk/rock/metal **Titanium**.

## Funcionalidad

- Vídeo de introducción a pantalla completa que, al terminar, se funde con
  el resto de la app.
- Cabecera con el nombre, presentación y localización de la asociación.
- **Próximos eventos**: tarjetas desplegables con fecha, grupo, descripción
  musical, logo del grupo y enlace opcional a sus redes sociales.
- **Nuestra sede**: fotos del local con descripción.
- **Galería**: fotos y vídeos, organizados en pestañas, cada uno con su
  texto descriptivo.
- **Hazte socio**: botón que abre un chat de WhatsApp con un número y
  mensaje predefinidos.

Todo el contenido (textos, eventos, imágenes y vídeos) se gestiona sin
tocar código, editando un fichero de texto y añadiendo ficheros a dos
carpetas. Ver **[CONTENIDO.md](CONTENIDO.md)** para la guía completa,
pensada para quien no sea programador/a.

## Stack técnico

- Kotlin, Jetpack Compose (Material 3)
- Media3 / ExoPlayer para la reproducción de vídeo
- kotlinx.serialization para leer `content.json`
- minSdk 24, compileSdk/targetSdk 34

## Abrir y ejecutar el proyecto

1. Abre la carpeta raíz de este repositorio con Android Studio (versión
   reciente, con Android SDK 34 instalado).
2. Deja que Android Studio sincronice Gradle.
3. Pulsa ▶ (Run) para instalar la app en un emulador o dispositivo.

También se puede compilar por línea de comandos (requiere tener
`ANDROID_HOME` apuntando a un SDK de Android con la plataforma 34 y
build-tools 34.0.0 instalados):

```bash
./gradlew assembleDebug
```

El APK resultante queda en `app/build/outputs/apk/debug/app-debug.apk`.

Este proyecto se ha verificado compilando `assembleDebug` y `lintDebug`
(0 errores) en el entorno de build; no se ha podido probar la interfaz en
un emulador/dispositivo real desde esta sesión. Antes de publicarla,
pruébala en un emulador o móvil Android para validar la experiencia
completa (vídeo, desplegables, WhatsApp, etc.).

## Estructura del proyecto

```
app/src/main/java/com/titanium/app/
  MainActivity.kt
  data/              <- modelos de contenido + lectura de content.json
  ui/theme/          <- colores y tipografía (estética punk/metal oscura)
  ui/components/     <- tarjetas y secciones (eventos, sede, galería, socios...)
  ui/screens/        <- pantalla principal (vídeo intro + contenido)
app/src/main/assets/
  content.json       <- todos los textos y referencias a imágenes/vídeos
  videos/            <- vídeo de intro y vídeos de la galería
app/src/main/res/drawable/
                      <- logos de grupos, fotos de sede y de galería
```

## Pendiente de aportar

- El vídeo de introducción real (`assets/videos/intro.mp4`).
- El número de WhatsApp real de la asociación (en `content.json`, ahora
  tiene un número de ejemplo `34600000000`).
- Los eventos, fotos y logos reales (ahora hay datos de ejemplo que se
  pueden editar o borrar siguiendo CONTENIDO.md).
- Un icono de app definitivo (ahora hay un icono provisional con un rayo).
