# Repartir versiones de prueba sin Play Store (Firebase App Distribution)

Esto permite que las administradoras reciban un aviso y un enlace para
instalar cada nueva versión de la app, sin que tengas que reenviarles el
`.apk` a mano cada vez.

## 1. Crear la credencial para poder subir versiones (una sola vez)

Esta herramienta sube los builds con una **cuenta de servicio** de Google
Cloud (no con tu usuario personal). Se crea una vez:

1. Entra en [console.cloud.google.com](https://console.cloud.google.com) y selecciona tu mismo proyecto de Firebase (arriba, el selector de proyecto — verás el mismo nombre/ID que en Firebase).
2. Menú ☰ → **IAM y administración** → **Cuentas de servicio** → **Crear cuenta de servicio**.
3. Ponle un nombre, por ejemplo `app-distribution-uploader`, y continúa.
4. En "Otorgar acceso", añade el rol **Administrador de Firebase App Distribution** (*Firebase App Distribution Admin*).
5. Termina la creación. Luego, sobre esa cuenta de servicio → pestaña **Claves** → **Agregar clave** → **Crear clave nueva** → tipo **JSON** → se descarga un fichero.
6. Renombra ese fichero a `app-distribution-service-account.json` y colócalo en la carpeta `app/` del proyecto (junto a `google-services.json`). **No lo subas a GitHub** (ya está excluido en `.gitignore`).

## 2. Añade a las personas que van a probar la app

En [console.firebase.google.com](https://console.firebase.google.com) →
tu proyecto → menú lateral **App Distribution** → pestaña **Testers y
grupos** → añade sus emails (presidenta, community manager, tú...).

## 3. Sube una nueva versión

Cada vez que quieras repartir una versión nueva, desde una terminal en la
carpeta del proyecto:

1. (Opcional pero recomendado) Edita `app/release_notes.txt` con un
   resumen de lo que ha cambiado.
2. Ejecuta:
   ```bash
   ./gradlew assembleDebug appDistributionUploadDebug
   ```
3. Las personas que hayas añadido como testers reciben un email con un
   enlace para descargar e instalar la nueva versión. La primera vez,
   su móvil les pedirá permiso para "instalar apps de orígenes
   desconocidos" para el Gmail/navegador desde el que abran el enlace —
   es normal, hay que aceptarlo.

## 4. Repetir

Para cada actualización, solo hace falta repetir el paso 3 (el paso 1 es
único, y el paso 2 solo hace falta repetirlo si se añade a alguien nuevo).

---

Esto reparte únicamente la versión de **debug** de la app (la misma que
usáis ahora para probar). Si en el futuro publicáis una versión firmada
de verdad para producción, se puede configurar igual para la versión
`release`.
