package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.Ocorrencia
import com.example.domain.model.ReportStatuses
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.HeroPill
import com.example.ui.components.InfoNoteBox
import com.example.ui.components.StatusTag
import com.example.ui.state.CitizenTechUiState
import com.example.ui.theme.LocalCitizenTechColors

@Composable
fun ReportsScreen(
    uiState: CitizenTechUiState,
    onFilterSelect: (String) -> Unit,
    onNewReportClick: () -> Unit,
    onDeleteReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    isAdmin: Boolean = false,
    onAdvanceStatus: (id: String, novoStatus: String) -> Unit = { _, _ -> },
) {
    val colors = LocalCitizenTechColors.current
    val filtered = uiState.filteredOcorrencias
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            containerColor = colors.cardSurface,
            title = { Text("Cancelar relato?", color = colors.navy) },
            text = {
                Text(
                    text = "O registro ${pendingDeleteId} será removido da sua lista.",
                    color = colors.ink,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingDeleteId?.let(onDeleteReport)
                        pendingDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.red),
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text("Manter", color = colors.navy)
                }
            },
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column {
                    EyebrowText(text = "ACOMPANHAMENTO")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (isAdmin) "Relatos da cidade" else "Meus relatos",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.navy,
                        )
                        HeroPill(text = "${uiState.ocorrencias.size} REGISTROS")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAdmin) {
                            "Perfil administrador: avance o andamento dos relatos de todos os cidadãos."
                        } else {
                            "Acompanhe o andamento das barreiras que você compartilhou."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.muted,
                    )
                }
            }

            item {
                Button(
                    onClick = onNewReportClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reports_new_button"),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.teal,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Novo relato", fontWeight = FontWeight.ExtraBold)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ReportStatuses.filters.forEach { filter ->
                        val active = uiState.selectedReportFilter == filter
                        FilterChip(
                            selected = active,
                            onClick = { onFilterSelect(filter) },
                            modifier = Modifier.testTag("report_filter_$filter"),
                            label = {
                                Text(
                                    text = filter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.navy,
                                selectedLabelColor = Color.White,
                                containerColor = colors.cardSurface,
                                labelColor = colors.muted,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = active,
                                borderColor = colors.border,
                                selectedBorderColor = colors.navy,
                            ),
                            shape = RoundedCornerShape(999.dp),
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    CitizenTechCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                                contentDescription = null,
                                tint = colors.muted,
                                modifier = Modifier.size(32.dp),
                            )
                            Text(
                                text = "Nenhum relato nesta categoria.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.muted,
                            )
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { report ->
                    ReportCard(
                        report = report,
                        onCancelClick = { pendingDeleteId = report.id },
                        isAdmin = isAdmin,
                        onAdvanceStatus = onAdvanceStatus,
                    )
                }
            }

            item {
                InfoNoteBox(
                    text = "Status possíveis: recebida, em análise, encaminhada, resolvida ou não confirmada. Os prazos de atendimento dependem do órgão responsável."
                )
            }
        }
    }
}

@Composable
private fun ReportCard(
    report: Ocorrencia,
    onCancelClick: () -> Unit,
    isAdmin: Boolean,
    onAdvanceStatus: (id: String, novoStatus: String) -> Unit,
) {
    val colors = LocalCitizenTechColors.current
    val progress = (report.progresso.coerceIn(4, 100)) / 100f

    CitizenTechCard(modifier = Modifier.testTag("report_card_${report.id}")) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = report.id,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                    color = colors.muted,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    StatusTag(text = report.status)
                    if (isAdmin || report.status.equals("Recebida", ignoreCase = true)) {
                        IconButton(
                            onClick = onCancelClick,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Cancelar relato ${report.id}",
                                tint = colors.muted,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${report.categoria} · ${report.localNome}",
                style = MaterialTheme.typography.titleSmall,
                color = colors.navy,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = report.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
            )

            if (!report.fotoUriLocal.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = report.fotoUriLocal,
                    contentDescription = "Foto anexada ao relato ${report.id}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Enviado: ${report.criadoEm}",
                    fontSize = 11.sp,
                    color = colors.muted,
                )
                Text(
                    text = "${report.progresso}% do fluxo",
                    fontSize = 11.sp,
                    color = colors.muted,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFFE8EFEC)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .background(colors.teal),
                )
            }

            val proximo = ReportStatuses.proximo(report.status)
            if (isAdmin && proximo != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onAdvanceStatus(report.id, proximo) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("report_advance_${report.id}"),
                    shape = RoundedCornerShape(11.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.navy,
                        contentColor = Color.White,
                    ),
                ) {
                    Text("Marcar como \"$proximo\"", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
