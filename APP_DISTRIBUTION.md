# Repartir versiones de prueba sin Play Store (Firebase App Distribution)

Esto permite que las administradoras reciban un aviso y un enlace para
instalar cada nueva versión de la app, sin que tengas que reenviarles el
`.apk` a mano cada vez. Todo esto se hace con la **herramienta de línea de
comandos de Firebase** (`firebase-tools`), iniciando sesión con tu cuenta
normal de Google (la misma con la que entras en Firebase Console) — **sin
tocar Google Cloud Console ni dar ninguna tarjeta**.

## 1. Instalar la herramienta (una sola vez por ordenador)

Necesitas tener [Node.js](https://nodejs.org) instalado. Luego, en una
terminal:

```bash
npm install -g firebase-tools
```

## 2. Iniciar sesión (una sola vez por ordenador)

```bash
firebase login
```

Esto abre el navegador para iniciar sesión con tu cuenta de Google, igual
que cuando entras en [console.firebase.google.com](https://console.firebase.google.com).
No pide tarjeta ni nada parecido.

## 3. Añadir a las personas que van a probar la app

En Firebase Console → tu proyecto → menú lateral **App Distribution** →
pestaña **Testers y grupos** → crea un grupo (por ejemplo `administradoras`)
y añade sus emails.

## 4. Subir una nueva versión

Cada vez que quieras repartir una versión nueva, desde la carpeta del
proyecto:

1. (Opcional pero recomendado) Edita `app/release_notes.txt` con un resumen de qué ha cambiado.
2. Genera el APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Súbelo y repártelo:
   ```bash
   firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
     --app 1:856031573548:android:0cdfe1f605cddd5226c389 \
     --groups "administradoras" \
     --release-notes-file app/release_notes.txt
   ```
   (Cambia `"administradoras"` por el nombre del grupo que hayas creado en el paso 3.)
3. Las personas del grupo reciben un email con un enlace para descargar e
   instalar la nueva versión. La primera vez, su móvil les pedirá permiso
   para "instalar apps de orígenes desconocidos" para el Gmail/navegador
   desde el que abran el enlace — es normal, hay que aceptarlo.

## 5. Repetir

Para cada actualización, basta con repetir el paso 4 (los pasos 1-3 son
únicos, y el 3 solo hace falta repetirlo si se añade a alguien nuevo).

---

El `1:856031573548:android:...` del paso 4 es el identificador de la app
dentro de vuestro proyecto de Firebase (no es secreto, está también en
`google-services.json` como `mobilesdk_app_id`); si alguna vez creáis otra
app o proyecto, ese número cambia y hay que cogerlo de ahí.
