package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.domain.model.BarrierCategories
import com.example.domain.model.LocalAcessivel
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.InfoNoteBox
import com.example.ui.theme.LocalCitizenTechColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(
    locais: List<LocalAcessivel>,
    preselectedLocalId: String?,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmit: (localId: String, categoria: String, descricao: String, fotoUri: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current

    var selectedLocalId by rememberSaveable { mutableStateOf(preselectedLocalId ?: "") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }

    var localExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(preselectedLocalId) {
        if (!preselectedLocalId.isNullOrBlank() && selectedLocalId.isBlank()) {
            selectedLocalId = preselectedLocalId
        }
    }

    BackHandler(onBack = onBack)

    // Zero-permission Android Photo Picker (Google Play policy compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { selectedPhotoUri = it.toString() }
        },
    )

    val selectedLocalName = locais.find { it.id == selectedLocalId }?.nome ?: ""

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("new_report_back_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colors.tealDark,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Voltar para meus relatos",
                        color = colors.tealDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                    )
                }
            }

            item {
                Column {
                    EyebrowText(text = "PARTICIPAÇÃO CIDADÃ")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encontrou uma barreira?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.navy,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Seu relato ajuda outras pessoas a planejar o trajeto. A foto é opcional.",
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
                        // Local dropdown
                        Column {
                            FieldLabel(label = "Local", required = true)
                            ExposedDropdownMenuBox(
                                expanded = localExpanded,
                                onExpandedChange = { localExpanded = !localExpanded },
                            ) {
                                OutlinedTextField(
                                    value = selectedLocalName,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Selecione o local", color = colors.muted) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = localExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                        .testTag("report_local_dropdown"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = reportFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = localExpanded,
                                    onDismissRequest = { localExpanded = false },
                                ) {
                                    locais.forEach { local ->
                                        DropdownMenuItem(
                                            text = { Text(local.nome) },
                                            onClick = {
                                                selectedLocalId = local.id
                                                localExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        // Category dropdown
                        Column {
                            FieldLabel(label = "Tipo de barreira", required = true)
                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = { categoryExpanded = !categoryExpanded },
                            ) {
                                OutlinedTextField(
                                    value = selectedCategory,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Selecione uma categoria", color = colors.muted) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                        .testTag("report_category_dropdown"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = reportFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false },
                                ) {
                                    BarrierCategories.all.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                selectedCategory = cat
                                                categoryExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        // Description
                        Column {
                            FieldLabel(label = "Descreva o que aconteceu", required = true)
                            OutlinedTextField(
                                value = description,
                                onValueChange = { if (it.length <= 500) description = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(128.dp)
                                    .testTag("report_description_input"),
                                placeholder = {
                                    Text(
                                        text = "Ex.: a rampa da entrada está bloqueada por um veículo...",
                                        color = colors.muted,
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = reportFieldColors(),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Mínimo de 12 caracteres. Não registre dados pessoais de terceiros.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "${description.length}/500",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (description.trim().length >= 12) colors.tealDark else colors.muted,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Optional photo using Android Photo Picker
                        Column {
                            FieldLabel(label = "Foto (opcional)", required = false)
                            if (selectedPhotoUri != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, colors.border),
                                    color = colors.infoNoteBg,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        AsyncImage(
                                            model = selectedPhotoUri,
                                            contentDescription = "Foto selecionada da barreira",
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop,
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Foto anexada localmente",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.navy,
                                            )
                                            Text(
                                                text = "Nesta versão demonstrativa, a foto fica apenas no dispositivo.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.muted,
                                            )
                                        }
                                        IconButton(onClick = { selectedPhotoUri = null }) {
                                            Icon(
                                                imageVector = Icons.Outlined.DeleteOutline,
                                                contentDescription = "Remover foto",
                                                tint = colors.red,
                                            )
                                        }
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("report_pick_photo_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, colors.border),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = colors.tealDark,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Selecionar foto da galeria",
                                        color = colors.navy,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nesta versão demonstrativa, a foto não é enviada à API.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.muted,
                                )
                            }
                        }

                        // Action buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = onBack,
                                enabled = !isSubmitting,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("report_cancel_button"),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.mint,
                                    contentColor = colors.tealDark,
                                ),
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.ExtraBold)
                            }
                            Button(
                                onClick = {
                                    onSubmit(
                                        selectedLocalId,
                                        selectedCategory,
                                        description,
                                        selectedPhotoUri,
                                    )
                                },
                                enabled = !isSubmitting,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("report_submit_button"),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.teal,
                                    contentColor = Color.White,
                                ),
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp),
                                    )
                                } else {
                                    Text("Enviar relato", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                InfoNoteBox(
                    text = "Seu relato será identificado como recebido. Ele não será tratado automaticamente como informação confirmada."
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(label: String, required: Boolean) {
    val colors = LocalCitizenTechColors.current
    Row(
        modifier = Modifier.padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.navy,
        )
        if (required) {
            Text(
                text = " *",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.red,
            )
        }
    }
}

@Composable
private fun reportFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = LocalCitizenTechColors.current.cardSurface,
    unfocusedContainerColor = LocalCitizenTechColors.current.cardSurface,
    focusedBorderColor = LocalCitizenTechColors.current.teal,
    unfocusedBorderColor = LocalCitizenTechColors.current.border,
    focusedTextColor = LocalCitizenTechColors.current.ink,
    unfocusedTextColor = LocalCitizenTechColors.current.ink,
)
