# Guía de contenidos de la app Titanium

Esta guía es para la persona que va a mantener actualizados los contenidos
de la app (eventos, fotos, vídeos, sede, etc.), aunque no sea programadora.
No hace falta tocar código Kotlin: basta con editar un fichero de texto y
añadir imágenes/vídeos en unas carpetas concretas.

Todo el contenido vive en:

```
app/src/main/assets/content.json   <- textos y referencias a imágenes/vídeos
app/src/main/assets/videos/        <- los vídeos (intro y galería)
app/src/main/res/drawable/         <- las fotos (logos de grupos, sede, galería)
```

Después de cambiar cualquier cosa, hay que **volver a generar la app**
(botón "Run"/▶ en Android Studio, o generar un nuevo APK) para que los
cambios se vean, ya que la app no tiene conexión a internet ni servidor:
todo el contenido va "dentro" de la app.

## 1. El vídeo de introducción

1. Pide o exporta el vídeo en formato `.mp4`.
2. Ponle el nombre `intro.mp4`.
3. Cópialo dentro de `app/src/main/assets/videos/`.
4. En `content.json`, comprueba que dice:
   ```json
   "intro": { "videoAsset": "intro.mp4" }
   ```

Si no hay ningún vídeo en esa carpeta, la app no falla: pasa directamente a
la pantalla principal con las tarjetas.

## 2. Presentación (nombre, texto y localización)

En `content.json`, bloque `presentation`:

```json
"presentation": {
  "name": "Titanium",
  "description": "Texto breve de presentación de la asociación...",
  "location": "Ciudad, dirección o zona donde está la asociación"
}
```

## 3. Próximos eventos

Cada concierto/evento es un bloque dentro de la lista `events`:

```json
{
  "id": "evento-unico-1",
  "day": "14",
  "month": "DIC",
  "bandName": "Nombre del grupo",
  "musicDescription": "Una o dos frases describiendo su música.",
  "logoDrawable": "logo_nombre_del_grupo",
  "socialUrl": "https://www.instagram.com/el_grupo"
}
```

- `id`: un texto único cualquiera (no se ve en la app, solo identifica el evento).
- `day` / `month`: la fecha, tal como se quiere mostrar (ej. "14" y "DIC").
- `bandName`: nombre del grupo.
- `musicDescription`: descripción breve del estilo musical (se muestra encima del logo).
- `logoDrawable`: nombre del fichero de imagen del logo del grupo que hayas
  puesto en `res/drawable/` **sin la extensión** (ver sección 6 sobre
  nombres de imágenes).
- `socialUrl`: enlace a redes sociales del grupo. Es opcional: si no hay,
  pon `null` (sin comillas) o borra la línea.

Para añadir otro evento, copia uno de los bloques, pégalo dentro de los
corchetes `[ ]` de `events`, separado por una coma, y cambia sus datos.
Para quitar un evento pasado, borra su bloque entero.

## 4. Nuestra sede

Bloque `venue.images`: una lista de fotos del local con su texto.

```json
"venue": {
  "images": [
    { "drawable": "sede_sala_principal", "caption": "Sala principal del local." },
    { "drawable": "sede_escenario", "caption": "Escenario y zona de barra." }
  ]
}
```

## 5. Galería (fotos y vídeos)

Bloque `gallery`, con dos listas: `photos` y `videos`.

```json
"gallery": {
  "photos": [
    { "drawable": "galeria_concierto_marzo", "caption": "Concierto de marzo 2025." }
  ],
  "videos": [
    {
      "videoAsset": "concierto_marzo.mp4",
      "thumbnailDrawable": "galeria_concierto_marzo_miniatura",
      "caption": "Resumen del concierto de marzo 2025."
    }
  ]
}
```

- Las fotos (`photos`) van directamente como imagen en `res/drawable/`.
- Los vídeos (`videos`) van como ficheros `.mp4` dentro de
  `assets/videos/` (igual que el vídeo de intro), y además necesitan una
  imagen de miniatura (`thumbnailDrawable`) en `res/drawable/` que se
  muestra con un icono de "play" encima; al tocarla, el vídeo se abre a
  pantalla completa.

## 6. Cómo añadir las imágenes (logos, fotos de sede y galería)

Las imágenes van en la carpeta:

```
app/src/main/res/drawable/
```

Reglas importantes del nombre del fichero (son reglas de Android, no se
pueden saltar):

- Solo letras minúsculas, números y guión bajo `_`.
- Nada de espacios, tildes, ñ, mayúsculas ni guiones `-`.
- Por ejemplo: `logo_grupo_ejemplo.png` es válido;
  `Logo Grupo Ejemplo.png` o `logo-grupo.png` darían error al compilar.

El nombre que se escribe en `content.json` (en `logoDrawable`, `drawable`
o `thumbnailDrawable`) es ese mismo nombre de fichero **sin la extensión**
(sin el `.png` o `.jpg` final).

Si en `content.json` se referencia un nombre que todavía no existe en
`res/drawable/`, la app no se rompe: muestra un icono de imagen genérico en
su lugar, para que puedas ir montando el contenido poco a poco.

## 7. Hazte socio (WhatsApp)

Bloque `membership`:

```json
"membership": {
  "intro": "Texto que invita a hacerse socio...",
  "whatsappNumber": "34600000000",
  "whatsappMessage": "Hola, quiero información para hacerme socio de Titanium"
}
```

- `whatsappNumber`: el número de WhatsApp de la asociación, con el
  prefijo de país y **sin** `+`, espacios ni guiones. Ejemplo para un
  número español 600 00 00 00: `"34600000000"`.
- `whatsappMessage`: el mensaje que aparecerá ya escrito cuando alguien
  pulse el botón y se abra WhatsApp.

## Resumen rápido

| Qué quieres hacer                         | Dónde                                              |
|--------------------------------------------|-----------------------------------------------------|
| Cambiar textos, fechas, enlaces            | `assets/content.json`                              |
| Poner el vídeo de intro o de un vídeo de galería | `assets/videos/`                              |
| Poner logos de grupos, fotos de sede o galería | `res/drawable/` (nombre en minúsculas y `_`)    |
| Cambiar el número de WhatsApp              | `assets/content.json`, bloque `membership`         |

Después de cualquier cambio: vuelve a generar/instalar la app para verlo.
