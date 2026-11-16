package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.EyebrowText
import com.example.ui.components.InfoNoteBox
import com.example.ui.theme.LocalCitizenTechColors

/**
 * Login e cadastro na API do grupo. O token JWT recebido fica salvo na sessão (DataStore)
 * e é enviado no cabeçalho Authorization de todas as chamadas seguintes.
 */
@Composable
fun LoginScreen(
    apiBaseUrl: String,
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (baseUrl: String, email: String, senha: String) -> Unit,
    onRegister: (baseUrl: String, nome: String, email: String, senha: String) -> Unit,
    onDemoMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current
    var criandoConta by rememberSaveable { mutableStateOf(false) }
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    var baseUrl by rememberSaveable(apiBaseUrl) { mutableStateOf(apiBaseUrl) }
    var mostrarServidor by rememberSaveable { mutableStateOf(false) }

    val campoCores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.teal,
        unfocusedBorderColor = colors.border,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            EyebrowText(text = "CitizenTech")
            Text(
                text = if (criandoConta) "Crie sua conta" else "Entre para continuar",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.navy,
            )
            Text(
                text = "Mais informação. Mais autonomia. Consulte locais, relate barreiras e receba alertas.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.muted,
            )

            CitizenTechCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (criandoConta) {
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it.take(100) },
                            label = { Text("Nome") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth().testTag("login_nome_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = campoCores,
                        )
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim().take(120) },
                        label = { Text("E-mail") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("login_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = campoCores,
                    )
                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it.take(72) },
                        label = { Text("Senha") },
                        singleLine = true,
                        visualTransformation = if (mostrarSenha) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        trailingIcon = {
                            TextButton(onClick = { mostrarSenha = !mostrarSenha }) {
                                Text(if (mostrarSenha) "Ocultar" else "Mostrar", color = colors.tealDark)
                            }
                        },
                        supportingText = if (criandoConta) {
                            { Text("Mínimo 8 caracteres, com letras e números.") }
                        } else {
                            null
                        },
                        modifier = Modifier.fillMaxWidth().testTag("login_senha_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = campoCores,
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            color = colors.red,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .testTag("login_error")
                                .semantics { liveRegion = LiveRegionMode.Polite },
                        )
                    }

                    Button(
                        onClick = {
                            if (criandoConta) onRegister(baseUrl, nome, email, senha)
                            else onLogin(baseUrl, email, senha)
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.teal,
                            contentColor = Color.White,
                        ),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp),
                            )
                        } else {
                            Text(
                                text = if (criandoConta) "Criar conta" else "Entrar",
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }

                    TextButton(
                        onClick = { criandoConta = !criandoConta },
                        modifier = Modifier.fillMaxWidth().testTag("login_toggle_mode"),
                    ) {
                        Text(
                            text = if (criandoConta) "Já tenho conta: entrar" else "Não tem conta? Cadastre-se",
                            color = colors.navy,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            TextButton(onClick = { mostrarServidor = !mostrarServidor }) {
                Text(
                    text = if (mostrarServidor) "Ocultar endereço da API" else "Configurar endereço da API",
                    color = colors.muted,
                )
            }
            if (mostrarServidor) {
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it.trim() },
                    label = { Text("URL-base da API") },
                    placeholder = { Text("http://10.0.2.2:8080/api/") },
                    supportingText = { Text("Emulador: 10.0.2.2 aponta para o computador. Celular: use o IP do PC na rede.") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth().testTag("login_api_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = campoCores,
                )
            }

            OutlinedButton(
                onClick = onDemoMode,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("login_demo_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, colors.border),
            ) {
                Text("Explorar no modo demonstração (sem API)", color = colors.navy, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))
            InfoNoteBox(
                text = "Contas de teste: ana@citizentech.com / Cidadao@2026 (cidadã) e admin@citizentech.com / Admin@2026 (administrador)."
            )
            Text(
                text = "Sua senha é enviada apenas para a API e armazenada lá com hash. O app guarda só o token de acesso.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
