package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AccessibleForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.PreferenciasUsuario
import com.example.ui.theme.LocalCitizenTechColors

@Composable
fun RouteGuidanceDialog(
    local: LocalAcessivel,
    preferencias: PreferenciasUsuario,
    onDismiss: () -> Unit,
) {
    val colors = LocalCitizenTechColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.AccessibleForward,
                    contentDescription = null,
                    tint = colors.teal,
                )
                Text(
                    text = "Orientação de trajeto",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.navy,
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Resumo personalizado para ${local.nome}:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                )

                if (preferencias.priorizarRampas || preferencias.evitarEscadas) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.mint,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Suas preferências ativas:",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.tealDark,
                            )
                            if (preferencias.evitarEscadas) {
                                Text(
                                    text = "• Evitar escadas e priorizar acesso nivelado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.ink,
                                )
                            }
                            if (preferencias.priorizarRampas) {
                                Text(
                                    text = "• Priorizar locais com rampas cadastradas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.ink,
                                )
                            }
                        }
                    }
                }

                if (local.recursos.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = colors.green,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Recursos disponíveis: ${local.recursos.take(2).joinToString(", ")}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.ink,
                        )
                    }
                }

                if (local.obstaculos.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = colors.amber,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Atenção aos relatos recentes: ${local.obstaculos.joinToString(", ")} (não confirmado).",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.amber,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Verifique rampas, calçadas e ocupação (${local.ocupacao} pessoas estimadas) antes de iniciar o deslocamento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.teal),
            ) {
                Text("Entendi")
            }
        },
    )
}
