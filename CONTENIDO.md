# Guía de administradores de la app Titanium

Desde esta versión, el contenido de la app (eventos, sede, galería,
presentación y contacto) ya **no se edita en ficheros**: se gestiona
desde dentro de la propia app, con una cuenta de administrador.

Antes de poder usar nada de esto, hay que completar la puesta en marcha
de Firebase (una sola vez) — ver **[FIREBASE_SETUP.md](FIREBASE_SETUP.md)**.

## Cómo entrar como administrador

1. Abre la app.
2. En la pantalla principal, **mantén pulsado el nombre "TITANIUM"** de la cabecera (es un punto de entrada escondido a propósito, para que el público no lo vea).
3. Si es la primera vez y tu email ya ha sido autorizado por otro administrador (o eres la presidenta, dada de alta en el paso 6 de FIREBASE_SETUP.md), pulsa **"Es la primera vez, quiero registrarme"**, escribe ese mismo email y una contraseña a tu elección.
4. Las siguientes veces, inicia sesión directamente con ese email y esa contraseña.

Si tu cuenta no está autorizada todavía, la app te lo dirá y no verás los botones de edición — pide a otro administrador que te añada (ver más abajo).

## Dar o quitar permisos a otro administrador

Dentro del panel de administración, pulsa **"Gestionar administradores"** (solo lo ven quienes tengan ese permiso especial, normalmente la presidenta).

- **Dar permisos**: escribe el email de la persona y pulsa "Autorizar este email como administrador". Después, esa persona tiene que entrar en la app y registrarse con ese mismo email (paso 3 de arriba).
- **Quitar permisos**: en la lista de administradores, pulsa "Quitar permisos" junto a su nombre. Deja de poder editar nada al instante, aunque conserve su cuenta.
- **Eliminar del todo**: pulsa "Eliminar" si ya no quieres que aparezca ni en la lista.
- El interruptor "También puede gestionar administradores" decide si esa persona, además de editar contenido, puede a su vez dar/quitar permisos a otros. Dejadlo desactivado salvo que de verdad haga falta.

## Añadir/editar contenido

Desde el panel de administración:

- **Próximos eventos**: un evento es una fecha (día y mes) con uno o varios grupos actuando ese día. Primero pones el día y el mes, luego vas añadiendo los grupos uno a uno (nombre + el cartel del concierto, se elige una foto del móvil y se sube sola) con "Añadir grupo a este evento", y cuando los tengas todos, pulsas "Añadir evento" para guardarlo. Para editar un evento ya creado, pulsa "Editar" en su tarjeta: carga sus grupos para que puedas quitar alguno o añadir más, y pulsa "Guardar cambios".
- **Nuestra sede**: añade una foto del local con un texto. Para quitar una, pulsa "Eliminar" en su tarjeta.
- **Galería → Fotos**: igual que la sede, foto + texto.
- **Galería → Vídeos (enlace)**: aquí **no se sube el vídeo**, solo se pega el enlace a la publicación ya subida al Instagram o Facebook de la asociación, con un texto y, opcionalmente, una miniatura. Al tocarlo en la app, se abre ese enlace. Esto es así para no generar coste de almacenamiento/ancho de banda por los vídeos.
- **Presentación y contacto**: aquí se cambia el nombre, el texto de presentación, la localización, el texto de "Hazte socio" y el enlace de Instagram del botón "Escríbenos en Instagram".

Todo se guarda al momento: en cuanto pulsas "Añadir"/"Guardar", ya lo ve cualquiera que abra la app (no hace falta volver a instalar nada).

## El vídeo de introducción

Ese es el único contenido que sigue sin gestionarse desde el panel: el vídeo que se ve al abrir la app (antes de las tarjetas) sigue viniendo incluido dentro de la propia app, en `app/src/main/assets/videos/intro.mp4`, porque así no genera ningún coste de ancho de banda (se descarga una sola vez, al instalar la app). Para cambiarlo hay que sustituir ese fichero y volver a generar la app.
