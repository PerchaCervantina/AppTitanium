# Puesta en marcha de Firebase (una sola vez)

Todo el código ya está preparado. Solo quedan unos pasos manuales que
necesitan tu cuenta de Google — nadie puede hacerlos por ti, pero se
pueden hacer desde el navegador del móvil, no hace falta ordenador.

## 1. Crear el proyecto de Firebase

1. Entra en [console.firebase.google.com](https://console.firebase.google.com) con la cuenta de Google que vaya a gestionar esto (puede ser la tuya o una de la asociación).
2. **Añadir proyecto** → ponle un nombre, por ejemplo `Titanium`.
3. No hace falta activar Google Analytics si no lo queréis.

## 2. Añadir la app Android al proyecto

1. En la página principal del proyecto, pulsa el icono de Android (</>) para "Añadir app".
2. **Nombre del paquete de Android**: escribe exactamente `com.titanium.app`
3. El alias/apodo puede ser cualquier cosa (ej. "Titanium Android").
4. No hace falta el SHA-1 para lo que usamos aquí.
5. Descarga el fichero **`google-services.json`** que te ofrece.
6. Ese fichero hay que colocarlo en la carpeta `app/` del proyecto (junto a `app/build.gradle.kts`), sustituyendo cualquier fichero de prueba que pudiera haber. Cuando estés en el ordenador con el proyecto abierto, cópialo ahí (o pídemelo y lo hago yo si me pasas el fichero).

## 3. Activar Authentication (email/contraseña)

1. Menú lateral → **Authentication** → **Comenzar**.
2. Pestaña **Sign-in method** → activa **Correo electrónico/contraseña**.

## 4. Activar Firestore Database

1. Menú lateral → **Firestore Database** → **Crear base de datos**.
2. Modo: **producción** (no "modo de prueba", porque vamos a pegar nuestras propias reglas).
3. Elige una región (cualquiera de Europa, ej. `eur3`).
4. Cuando esté creada: pestaña **Reglas** → borra lo que haya y pega el contenido completo del fichero [`firestore.rules`](firestore.rules) de este repositorio → **Publicar**.

## 5. Las fotos: Cloudinary, no Firebase Storage

Firebase Storage ahora exige el plan de pago **Blaze** incluso para el
nivel gratuito, así que las fotos (logos de grupos, sede, galería) se
suben a **Cloudinary**, que tiene un plan gratuito real sin pedir tarjeta.
Es un paso aparte, con su propia guía: **[CLOUDINARY_SETUP.md](CLOUDINARY_SETUP.md)**.
Firebase se queda solo con Authentication y Firestore (ambos gratis sin
necesidad de Blaze).

## 6. Dar de alta al primer administrador (la presidenta)

Esto es el único "huevo y gallina" del sistema: la primera persona no
puede añadirla otro administrador porque todavía no existe ninguno. Se
hace una vez, a mano, desde la consola:

1. **Authentication** → pestaña **Users** → **Add user** → escribe el email y una contraseña para la presidenta. (Esto crea su cuenta directamente; luego ella podrá entrar en la app con ese email y esa contraseña sin tener que "registrarse" otra vez).
2. **Firestore Database** → **Iniciar colección** → id de la colección: `admins`.
3. Id del documento: el mismo email, **en minúsculas**, exactamente como lo escribiste en el paso 1 (ej. `presidenta@email.com`).
4. Añade estos 3 campos al documento:
   - `active` (tipo booleano) = `true`
   - `canManageAdmins` (tipo booleano) = `true`
   - `addedBy` (tipo string) = `setup inicial`
5. Guardar.

A partir de aquí, la presidenta ya puede entrar en la app (mantener pulsado el nombre "TITANIUM" en la pantalla principal → iniciar sesión) y, desde "Gestionar administradores", dar de alta a la community manager y a quien haga de respaldo, siguiendo el flujo normal (ver CONTENIDO.md).

## 7. Comprobar que compila

Con el fichero `google-services.json` real ya colocado en `app/` y Cloudinary configurado (paso 5), el proyecto debería compilar y ejecutarse en Android Studio igual que antes, pero ahora con las pantallas de administración funcionando de verdad.

---

Si en algún paso te atascas, dime exactamente en qué pantalla estás y qué ves, y te guío desde ahí.
