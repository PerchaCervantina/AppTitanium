package com.titanium.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.titanium.app.data.firebase.EventEntry
import com.titanium.app.data.firebase.FirestoreContentRepository
import com.titanium.app.data.firebase.GalleryPhoto
import com.titanium.app.data.firebase.GalleryVideoLink
import com.titanium.app.data.firebase.MembershipInfo
import com.titanium.app.data.firebase.PresentationInfo
import com.titanium.app.data.firebase.VenuePhoto
import com.titanium.app.ui.admin.AddPhotoDialog
import com.titanium.app.ui.admin.AddVideoLinkDialog
import com.titanium.app.ui.admin.AdminSession
import com.titanium.app.ui.admin.TranslucentIconButton
import com.titanium.app.ui.components.EventCard
import com.titanium.app.ui.components.GallerySection
import com.titanium.app.ui.components.HeaderCard
import com.titanium.app.ui.components.IntroVideoPlayer
import com.titanium.app.ui.components.MembershipSection
import com.titanium.app.ui.components.NewEventCard
import com.titanium.app.ui.components.SectionTitle
import com.titanium.app.ui.components.VenueSection
import com.titanium.app.util.assetExists
import kotlinx.coroutines.launch

private const val INTRO_VIDEO_ASSET = "videos/intro.mp4"

private enum class IntroPhase { VIDEO, LOGO_TRANSITION, DONE }

/**
 * Pantalla principal: vídeo de introducción a pantalla completa que, al
 * terminar, da paso a una pequeña animación en la que el logo del vídeo
 * "aterriza" en su sitio de la cabecera, y después aparece el resto del
 * contenido, leído en tiempo real desde Firestore. Si no hay vídeo de
 * intro, se salta directamente al contenido.
 *
 * Es la ÚNICA pantalla de la app: cuando hay sesión de administrador
 * ([session]), esta misma pantalla muestra los controles de edición en el
 * sitio (nada de navegar a otra pantalla para editar y tener que volver
 * para ver el resultado). También evita que el vídeo de intro se repita
 * al entrar/salir del modo administrador, porque esta pantalla nunca se
 * desmonta: el login y la gestión de administradores se muestran como
 * diálogos por encima.
 */
@Composable
fun HomeScreen(
    session: AdminSession,
    onAdminAccessRequested: () -> Unit,
    onManageAdminsRequested: () -> Unit
) {
    val context = LocalContext.current
    val hasIntroVideo = remember { assetExists(context, INTRO_VIDEO_ASSET) }

    var phase by remember { mutableStateOf(if (hasIntroVideo) IntroPhase.VIDEO else IntroPhase.DONE) }
    val contentAlpha = remember { Animatable(if (hasIntroVideo) 0f else 1f) }
    var logoTargetRect by remember { mutableStateOf<Rect?>(null) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // El contenido real se compone desde el principio (para poder
        // medir dónde cae el logo de la cabecera) pero permanece invisible
        // hasta que termina la animación de aterrizaje.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha.value }
        ) {
            MainContent(
                session = session,
                onAdminAccessRequested = onAdminAccessRequested,
                onManageAdminsRequested = onManageAdminsRequested,
                onHeaderLogoPositioned = { logoTargetRect = it }
            )
        }

        when (phase) {
            IntroPhase.VIDEO -> IntroVideoPlayer(
                assetPath = INTRO_VIDEO_ASSET,
                onFinished = { phase = IntroPhase.LOGO_TRANSITION }
            )

            IntroPhase.LOGO_TRANSITION -> {
                val target = logoTargetRect
                if (target != null) {
                    IntroLogoTransition(
                        startRect = introLogoStartRect(screenWidthPx, screenHeightPx),
                        targetRect = target,
                        contentAlpha = contentAlpha,
                        onSettled = { phase = IntroPhase.DONE }
                    )
                } else {
                    // No debería pasar (el contenido se mide casi al
                    // instante), pero por si acaso no nos quedamos
                    // atascados sin mostrar nada.
                    LaunchedEffect(Unit) {
                        contentAlpha.snapTo(1f)
                        phase = IntroPhase.DONE
                    }
                }
            }

            IntroPhase.DONE -> Unit
        }
    }
}

@Composable
private fun MainContent(
    session: AdminSession,
    onAdminAccessRequested: () -> Unit,
    onManageAdminsRequested: () -> Unit,
    onHeaderLogoPositioned: (Rect) -> Unit,
    repository: FirestoreContentRepository = remember { FirestoreContentRepository() }
) {
    val isAdmin = session.isAdmin
    val scope = rememberCoroutineScope()

    val presentation by repository.observePresentation().collectAsState(initial = PresentationInfo())
    val events by repository.observeEvents().collectAsState(initial = emptyList<EventEntry>())
    val venuePhotos by repository.observeVenuePhotos().collectAsState(initial = emptyList<VenuePhoto>())
    val galleryPhotos by repository.observeGalleryPhotos().collectAsState(initial = emptyList<GalleryPhoto>())
    val galleryVideoLinks by repository.observeGalleryVideoLinks().collectAsState(initial = emptyList<GalleryVideoLink>())
    val membership by repository.observeMembership().collectAsState(initial = MembershipInfo())

    var isCreatingEvent by remember { mutableStateOf(false) }
    var showAddVenuePhoto by remember { mutableStateOf(false) }
    var showAddGalleryPhoto by remember { mutableStateOf(false) }
    var showAddGalleryVideo by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        if (isAdmin) {
            item {
                AdminToolbar(
                    email = session.email.orEmpty(),
                    canManageAdmins = session.canManageAdmins,
                    onManageAdminsRequested = onManageAdminsRequested,
                    onLogout = session::signOut
                )
            }
        }

        item {
            HeaderCard(
                presentation = presentation,
                onAdminAccessRequested = onAdminAccessRequested,
                isAdmin = isAdmin,
                onSavePresentation = { scope.launch { repository.updatePresentation(it) } },
                onLogoPositioned = onHeaderLogoPositioned
            )
        }

        item {
            SectionTitle(
                text = "Próximos eventos",
                trailing = if (isAdmin) {
                    { TranslucentIconButton(icon = Icons.Default.Add, contentDescription = "Añadir evento") { isCreatingEvent = true } }
                } else null
            )
        }
        if (isCreatingEvent) {
            item {
                NewEventCard(
                    onSave = {
                        scope.launch {
                            repository.addOrUpdateEvent(it)
                            isCreatingEvent = false
                        }
                    },
                    onCancel = { isCreatingEvent = false }
                )
            }
        }
        items(events, key = { it.id }) { event ->
            EventCard(
                event = event,
                isAdmin = isAdmin,
                onSave = { scope.launch { repository.addOrUpdateEvent(it) } },
                onDelete = if (isAdmin) {
                    { scope.launch { repository.deleteEvent(event.id) } }
                } else null
            )
        }

        item {
            VenueSection(
                images = venuePhotos,
                modifier = Modifier.padding(top = 8.dp),
                isAdmin = isAdmin,
                onAddRequested = { showAddVenuePhoto = true },
                onDelete = { id -> scope.launch { repository.deleteVenuePhoto(id) } }
            )
        }

        item {
            GallerySection(
                photos = galleryPhotos,
                videoLinks = galleryVideoLinks,
                modifier = Modifier.padding(top = 8.dp),
                isAdmin = isAdmin,
                onAddPhotoRequested = { showAddGalleryPhoto = true },
                onDeletePhoto = { id -> scope.launch { repository.deleteGalleryPhoto(id) } },
                onAddVideoRequested = { showAddGalleryVideo = true },
                onDeleteVideo = { id -> scope.launch { repository.deleteGalleryVideoLink(id) } }
            )
        }

        item {
            MembershipSection(
                membership = membership,
                modifier = Modifier.padding(top = 8.dp),
                isAdmin = isAdmin,
                onSaveMembership = { scope.launch { repository.updateMembership(it) } }
            )
        }

        item { Box(modifier = Modifier.height(32.dp)) }
    }

    if (showAddVenuePhoto) {
        AddPhotoDialog(
            folder = "venue",
            onDismiss = { showAddVenuePhoto = false },
            onAdd = { caption, imageUrl ->
                scope.launch {
                    repository.addVenuePhoto(imageUrl, caption)
                    showAddVenuePhoto = false
                }
            }
        )
    }
    if (showAddGalleryPhoto) {
        AddPhotoDialog(
            folder = "gallery_photos",
            onDismiss = { showAddGalleryPhoto = false },
            onAdd = { caption, imageUrl ->
                scope.launch {
                    repository.addGalleryPhoto(imageUrl, caption)
                    showAddGalleryPhoto = false
                }
            }
        )
    }
    if (showAddGalleryVideo) {
        AddVideoLinkDialog(
            onDismiss = { showAddGalleryVideo = false },
            onAdd = { linkUrl, caption, thumbnailUrl ->
                scope.launch {
                    repository.addGalleryVideoLink(linkUrl, caption, thumbnailUrl)
                    showAddGalleryVideo = false
                }
            }
        )
    }
}

@Composable
private fun AdminToolbar(
    email: String,
    canManageAdmins: Boolean,
    onManageAdminsRequested: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Modo administrador · $email",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (canManageAdmins) {
            TranslucentIconButton(
                icon = Icons.Default.People,
                contentDescription = "Gestionar administradores",
                onClick = onManageAdminsRequested,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        TranslucentIconButton(
            icon = Icons.AutoMirrored.Filled.Logout,
            contentDescription = "Cerrar sesión",
            onClick = onLogout
        )
    }
}
