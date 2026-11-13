package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Alerta
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.DemoLabelPill
import com.example.ui.components.EyebrowText
import com.example.ui.components.InfoNoteBox
import com.example.ui.state.CitizenTechUiState
import com.example.ui.theme.LocalCitizenTechColors
import kotlin.math.roundToInt

@Composable
fun AlertsScreen(
    uiState: CitizenTechUiState,
    onSimulateDefaultSensors: () -> Unit,
    onSimulateCustomSensors: (ocupacao: Int, chuvaMmH: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    var showCustomDialog by remember { mutableStateOf(false) }

    if (showCustomDialog) {
        CustomSensorSimulationDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { occ, rain ->
                showCustomDialog = false
                onSimulateCustomSensors(occ, rain)
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
                    EyebrowText(text = "INFORMAÇÃO EM TEMPO DE PLANEJAR")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Alertas da região",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.navy,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Condições que podem exigir atenção antes ou durante o deslocamento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.muted,
                    )
                }
            }

            item {
                CitizenTechCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Sensores monitorados",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.navy,
                            )
                            DemoLabelPill(text = "SIMULAÇÃO IoT")
                        }
                        SensorDescriptionRow(
                            number = "1",
                            title = "Contagem de pessoas",
                            description = "Estima a ocupação de ambientes, sem identificar pessoas.",
                        )
                        SensorDescriptionRow(
                            number = "2",
                            title = "Chuva / risco natural",
                            description = "Identifica leituras acima do limite configurado para demonstração.",
                        )
                    }
                }
            }

            if (uiState.alertas.isEmpty()) {
                item {
                    CitizenTechCard {
                        Text(
                            text = "Nenhum alerta ativo na área demonstrativa.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.muted,
                        )
                    }
                }
            } else {
                items(uiState.alertas, key = { it.id }) { alert ->
                    AlertCard(alert = alert)
                }
            }

            item {
                Button(
                    onClick = onSimulateDefaultSensors,
                    enabled = !uiState.isSimulatingSensors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("alerts_simulate_button"),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.mint,
                        contentColor = colors.tealDark,
                    ),
                ) {
                    if (uiState.isSimulatingSensors) {
                        CircularProgressIndicator(
                            color = colors.tealDark,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrando leituras...", fontWeight = FontWeight.ExtraBold)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Simular novas leituras dos sensores",
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { showCustomDialog = true },
                    enabled = !uiState.isSimulatingSensors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("alerts_custom_simulate_button"),
                    shape = RoundedCornerShape(13.dp),
                    border = BorderStroke(1.dp, colors.border),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = colors.navy,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ajustar valores da simulação",
                        color = colors.navy,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                InfoNoteBox(
                    text = "Atenção: estes avisos são gerados por leituras fictícias para demonstração acadêmica. Não são alertas oficiais da Defesa Civil nem substituem orientações das autoridades."
                )
            }
        }
    }
}

@Composable
private fun SensorDescriptionRow(
    number: String,
    title: String,
    description: String,
) {
    val colors = LocalCitizenTechColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(colors.greenBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.green,
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.ink,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
            )
        }
    }
}

@Composable
private fun AlertCard(alert: Alerta) {
    val colors = LocalCitizenTechColors.current
    val valorFmt = if (alert.valor % 1.0 == 0.0) {
        alert.valor.roundToInt().toString()
    } else {
        alert.valor.toString()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alert_card_${alert.id}"),
        shape = RoundedCornerShape(18.dp),
        color = colors.cardSurface,
        border = BorderStroke(if (colors.isHighContrast) 2.dp else 1.dp, colors.border),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFCB7C22)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(15.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = alert.tipo.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = colors.amber,
                    )
                    DemoLabelPill(text = "DADO SIMULADO")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alert.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.navy,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = colors.muted,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = alert.local,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.muted,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = alert.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8EB),
                    border = BorderStroke(1.dp, Color(0xFFF2E2C6)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "LEITURA DO SENSOR",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF896B43),
                            )
                            Text(
                                text = "$valorFmt ${alert.unidade}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF8A500C),
                            )
                        }
                        Text(
                            text = "ATENÇÃO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF95611D),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Última leitura: ${alert.atualizadoEm}",
                        fontSize = 10.5.sp,
                        color = colors.muted,
                    )
                    Text(
                        text = "Não oficial",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomSensorSimulationDialog(
    onDismiss: () -> Unit,
    onConfirm: (ocupacao: Int, chuvaMmH: Double) -> Unit,
) {
    val colors = LocalCitizenTechColors.current
    var occupancy by remember { mutableFloatStateOf(42f) }
    var rain by remember { mutableFloatStateOf(35f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.cardSurface,
        title = {
            Text("Simular sensores IoT", color = colors.navy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Biblioteca Central · Ocupação: ${occupancy.roundToInt()} pessoas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                )
                Slider(
                    value = occupancy,
                    onValueChange = { occupancy = it },
                    valueRange = 5f..60f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.teal,
                        activeTrackColor = colors.teal,
                    ),
                )
                Text(
                    text = "Praça das Artes · Chuva: ${rain.roundToInt()} mm/h (alerta >= 30)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                )
                Slider(
                    value = rain,
                    onValueChange = { rain = it },
                    valueRange = 0f..70f,
                    colors = SliderDefaults.colors(
                        thumbColor = colors.amber,
                        activeTrackColor = colors.amber,
                    ),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(occupancy.roundToInt(), rain.roundToInt().toDouble()) },
                colors = ButtonDefaults.buttonColors(containerColor = colors.teal),
            ) {
                Text("Enviar leituras")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = colors.navy)
            }
        },
    )
}
