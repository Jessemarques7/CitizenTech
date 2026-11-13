package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.InfoNoteBox
import com.example.ui.state.CitizenTechUiState
import com.example.ui.theme.LocalCitizenTechColors
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    uiState: CitizenTechUiState,
    onToggleEvitarEscadas: () -> Unit,
    onTogglePriorizarRampas: () -> Unit,
    onToggleAlertasVisuais: () -> Unit,
    onToggleAltoContraste: () -> Unit,
    onFontDown: () -> Unit,
    onFontUp: () -> Unit,
    onSavePreferences: () -> Unit,
    onUpdateApiConnection: (useApiMode: Boolean, baseUrl: String, bearerToken: String) -> Unit,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    val prefs = uiState.preferencias
    val apiCfg = uiState.apiConfig

    var useApiModeInput by remember { mutableStateOf(apiCfg.useApiMode) }
    var baseUrlInput by remember { mutableStateOf(apiCfg.apiBaseUrl) }
    var tokenInput by remember { mutableStateOf(apiCfg.bearerToken) }

    LaunchedEffect(apiCfg) {
        useApiModeInput = apiCfg.useApiMode
        baseUrlInput = apiCfg.apiBaseUrl
        tokenInput = apiCfg.bearerToken
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
                    EyebrowText(text = "PERSONALIZAÇÃO")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seu jeito de circular",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.navy,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ajuste as informações para que o app priorize o que faz sentido para sua mobilidade.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.muted,
                    )
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = colors.navy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFD5EEEA)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "A",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.tealDark,
                            )
                        }
                        Column {
                            Text(
                                text = "Alex Silva",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                            )
                            Text(
                                text = if (apiCfg.useApiMode) {
                                    "Perfil cidadão · conectado à API"
                                } else {
                                    "Perfil cidadão · demonstração"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFBDD0CE),
                            )
                        }
                    }
                }
            }

            item {
                CitizenTechCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Preferências de trajeto",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.navy,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SettingSwitchRow(
                            title = "Evitar escadas",
                            description = "Priorizar caminhos sem degraus.",
                            checked = prefs.evitarEscadas,
                            onCheckedChange = { onToggleEvitarEscadas() },
                            testTag = "switch_evitar_escadas",
                        )
                        HorizontalDivider(color = colors.border)
                        SettingSwitchRow(
                            title = "Priorizar rampas",
                            description = "Dar destaque a locais com rampas cadastradas.",
                            checked = prefs.priorizarRampas,
                            onCheckedChange = { onTogglePriorizarRampas() },
                            testTag = "switch_priorizar_rampas",
                        )
                        HorizontalDivider(color = colors.border)
                        SettingSwitchRow(
                            title = "Alertas visuais e em texto",
                            description = "Não depender apenas de avisos sonoros.",
                            checked = prefs.alertasVisuais,
                            onCheckedChange = { onToggleAlertasVisuais() },
                            testTag = "switch_alertas_visuais",
                        )
                    }
                }
            }

            item {
                CitizenTechCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Acessibilidade da interface",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.navy,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SettingSwitchRow(
                            title = "Alto contraste",
                            description = "Aumentar o contraste entre texto e fundo.",
                            checked = prefs.altoContraste,
                            onCheckedChange = { onToggleAltoContraste() },
                            testTag = "switch_alto_contraste",
                        )
                        HorizontalDivider(color = colors.border)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Tamanho do texto",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.navy,
                                )
                                Text(
                                    text = "Escala atual: ${(prefs.fontScale * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = onFontDown,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, colors.border),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("profile_font_down"),
                                ) {
                                    Text("A−", fontWeight = FontWeight.ExtraBold, color = colors.navy)
                                }
                                OutlinedButton(
                                    onClick = onFontUp,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, colors.border),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("profile_font_up"),
                                ) {
                                    Text("A+", fontWeight = FontWeight.ExtraBold, color = colors.navy)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onSavePreferences,
                    enabled = !uiState.isSavingPreferences,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_save_button"),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.teal,
                        contentColor = Color.White,
                    ),
                ) {
                    if (uiState.isSavingPreferences) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                        )
                    } else {
                        Text("Salvar preferências", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            item {
                CitizenTechCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Integração API REST & Demonstração",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.navy,
                        )
                        SettingSwitchRow(
                            title = "Conectar à API REST do grupo",
                            description = "Desligado = modo demonstração local (Room). Ligado = chamadas HTTP reais.",
                            checked = useApiModeInput,
                            onCheckedChange = { useApiModeInput = it },
                            testTag = "switch_api_mode",
                        )
                        if (useApiModeInput) {
                            OutlinedTextField(
                                value = baseUrlInput,
                                onValueChange = { baseUrlInput = it },
                                label = { Text("URL-base da API") },
                                placeholder = { Text("http://10.0.2.2:8080/api/") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_api_url_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.teal,
                                    unfocusedBorderColor = colors.border,
                                ),
                            )
                            OutlinedTextField(
                                value = tokenInput,
                                onValueChange = { tokenInput = it },
                                label = { Text("Token Bearer (opcional)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_api_token_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.teal,
                                    unfocusedBorderColor = colors.border,
                                ),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = {
                                    onUpdateApiConnection(useApiModeInput, baseUrlInput, tokenInput)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("profile_apply_connection"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.navy,
                                    contentColor = Color.White,
                                ),
                            ) {
                                Text("Aplicar modo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            if (!apiCfg.useApiMode) {
                                OutlinedButton(
                                    onClick = onResetDemoData,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("profile_reset_demo"),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, colors.border),
                                ) {
                                    Text(
                                        text = "Restaurar demo",
                                        color = colors.navy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                InfoNoteBox(
                    text = "As preferências são aplicadas nesta demonstração e, com a API conectada, podem ser salvas no perfil do usuário."
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    val colors = LocalCitizenTechColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.navy,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.teal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFC7D3CF),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}
