package com.titanium.app.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.titanium.app.data.firebase.MembershipInfo

private data class ContactAction(val icon: ImageVector, val label: String, val onClick: () -> Unit)

@Composable
fun MembershipSection(membership: MembershipInfo, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        SectionTitle(text = "Hazte socio")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = membership.intro,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (membership.instagramUrl.isNotBlank()) {
                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, membership.instagramUrl.toUri()))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                        Text(text = "  Escríbenos en Instagram")
                    }
                }

                val contactActions = buildList {
                    if (membership.facebookUrl.isNotBlank()) {
                        add(
                            ContactAction(Icons.Default.ThumbUp, "Facebook") {
                                context.startActivity(Intent(Intent.ACTION_VIEW, membership.facebookUrl.toUri()))
                            }
                        )
                    }
                    if (membership.tiktokUrl.isNotBlank()) {
                        add(
                            ContactAction(Icons.Default.MusicNote, "TikTok") {
                                context.startActivity(Intent(Intent.ACTION_VIEW, membership.tiktokUrl.toUri()))
                            }
                        )
                    }
                    if (membership.email.isNotBlank()) {
                        add(
                            ContactAction(Icons.Default.Email, "Email") {
                                context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:${membership.email}".toUri()))
                            }
                        )
                    }
                }

                if (contactActions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        contactActions.forEach { action ->
                            OutlinedButton(onClick = action.onClick, modifier = Modifier.weight(1f)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = action.icon, contentDescription = action.label)
                                    Text(text = action.label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
