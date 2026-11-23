package com.titanium.app.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.titanium.app.R
import com.titanium.app.data.firebase.MembershipInfo
import com.titanium.app.ui.admin.TranslucentIconButton

private data class ContactAction(val icon: ImageVector, val label: String, val onClick: () -> Unit)

@Composable
fun MembershipSection(
    membership: MembershipInfo,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onSaveMembership: (MembershipInfo) -> Unit = {}
) {
    val context = LocalContext.current
    var editing by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SectionTitle(text = "Hazte socio")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (editing) {
                    MembershipEditForm(
                        initial = membership,
                        onSave = { onSaveMembership(it); editing = false },
                        onCancel = { editing = false }
                    )
                    return@Column
                }

                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = membership.intro,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isAdmin) {
                        TranslucentIconButton(icon = Icons.Default.Edit, contentDescription = "Editar contacto", onClick = { editing = true })
                    }
                }

                val instagramIcon = ImageVector.vectorResource(id = R.drawable.ic_instagram)
                val facebookIcon = ImageVector.vectorResource(id = R.drawable.ic_facebook)

                val mainContactActions = buildList {
                    if (membership.instagramUrl.isNotBlank()) {
                        add(
                            ContactAction(instagramIcon, "Instagram") {
                                context.startActivity(Intent(Intent.ACTION_VIEW, membership.instagramUrl.toUri()))
                            }
                        )
                    }
                    if (membership.facebookUrl.isNotBlank()) {
                        add(
                            ContactAction(facebookIcon, "Facebook") {
                                context.startActivity(Intent(Intent.ACTION_VIEW, membership.facebookUrl.toUri()))
                            }
                        )
                    }
                    if (membership.email.isNotBlank()) {
                        add(
                            ContactAction(Icons.Default.Email, "Gmail") {
                                context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:${membership.email}".toUri()))
                            }
                        )
                    }
                }

                if (mainContactActions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mainContactActions.forEach { action ->
                            OutlinedButton(onClick = action.onClick, modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = action.icon, contentDescription = action.label)
                                    Text(text = action.label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                if (membership.tiktokUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, membership.tiktokUrl.toUri()))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = "TikTok")
                        Text(text = "  TikTok")
                    }
                }
            }
        }
    }
}

@Composable
private fun MembershipEditForm(
    initial: MembershipInfo,
    onSave: (MembershipInfo) -> Unit,
    onCancel: () -> Unit
) {
    var intro by remember { mutableStateOf(initial.intro) }
    var instagramUrl by remember { mutableStateOf(initial.instagramUrl) }
    var facebookUrl by remember { mutableStateOf(initial.facebookUrl) }
    var tiktokUrl by remember { mutableStateOf(initial.tiktokUrl) }
    var email by remember { mutableStateOf(initial.email) }

    Column {
        Text(
            text = "Deja vacío un campo para que su botón no aparezca.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(intro, { intro = it }, label = { Text("Texto de invitación") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(instagramUrl, { instagramUrl = it }, label = { Text("Enlace de Instagram") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(facebookUrl, { facebookUrl = it }, label = { Text("Enlace de Facebook") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(tiktokUrl, { tiktokUrl = it }, label = { Text("Enlace de TikTok") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Correo electrónico de contacto") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        Row(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedButton(onClick = { onSave(MembershipInfo(intro, instagramUrl, facebookUrl, tiktokUrl, email)) }) { Text("Guardar") }
            TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    }
}
