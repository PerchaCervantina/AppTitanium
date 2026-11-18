# Puesta en marcha de Cloudinary (una sola vez, sin tarjeta)

Cloudinary es donde se guardan las fotos que suban los administradores
(logos de grupos, sede, galería). Tiene un plan gratuito real: no pide
tarjeta de crédito para registrarse ni para usarlo dentro de los límites
gratuitos (25 "créditos" al mes, de sobra para una asociación pequeña).

## 1. Crear la cuenta

1. Entra en [cloudinary.com](https://cloudinary.com) → **Sign up free**.
2. Regístrate con un email (puede ser el de la asociación). No pide tarjeta.

## 2. Anotar el "Cloud name"

1. Una vez dentro, en el **Dashboard** (la pantalla principal), verás un campo llamado **Cloud name**. Es un nombre corto, tipo `dxxxxxxxxx` o uno que hayas elegido.
2. Apúntalo, lo necesitamos en el paso 4.

## 3. Crear un "upload preset" sin firmar

Esto es lo que permite que la app suba fotos directamente desde el móvil
sin necesitar ninguna clave secreta ni servidor propio.

1. Menú lateral → **Settings** (el icono de engranaje) → pestaña **Upload**.
2. Busca la sección **Upload presets** → **Add upload preset**.
3. **Signing Mode**: cámbialo a **Unsigned** (muy importante, si no, la app no podrá subir fotos).
4. Puedes dejar el resto de opciones como están, o (opcional, recomendado) en **Folder** escribe `titanium` para que todo se guarde ordenado.
5. Guarda y apunta el **nombre del preset** (aparece arriba, algo como `xxxxxxxxx` o el que le hayas puesto tú).

## 4. Poner esos dos datos en el proyecto

Edita el fichero
[`app/src/main/java/com/titanium/app/data/cloudinary/CloudinaryConfig.kt`](app/src/main/java/com/titanium/app/data/cloudinary/CloudinaryConfig.kt)
y sustituye:

```kotlin
const val CLOUD_NAME = "PON_AQUI_TU_CLOUD_NAME"
const val UPLOAD_PRESET = "PON_AQUI_TU_UPLOAD_PRESET"
```

por tus dos valores reales (entre comillas, tal cual los tengas en
Cloudinary). Ninguno de los dos es secreto: están pensados para ir dentro
de la app.

## 5. Ya está

Con esos dos valores puestos y el proyecto compilado de nuevo, el botón
"elegir imagen" de las pantallas de administración ya sube las fotos de
verdad.

---

Si en algún paso no encuentras algo (Cloudinary cambia de vez en cuando el
sitio de los menús), dime qué ves en pantalla y te digo dónde mirar.
