package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.domain.model.ReportStatuses
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.CitizenTechTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `login envia email e senha digitados e mostra erro da API`() {
        var enviado: Pair<String, String>? = null
        composeRule.setContent {
            CitizenTechTheme(highContrast = false, userFontScale = 1f) {
                LoginScreen(
                    apiBaseUrl = "http://10.0.2.2:8080/api/",
                    isLoading = false,
                    errorMessage = "E-mail ou senha inválidos.",
                    onLogin = { _, email, senha -> enviado = email to senha },
                    onRegister = { _, _, _, _ -> },
                    onDemoMode = {},
                )
            }
        }

        composeRule.onNodeWithTag("login_email_input").performTextInput("ana@citizentech.com")
        composeRule.onNodeWithTag("login_senha_input").performTextInput("Cidadao@2026")
        composeRule.onNodeWithTag("login_submit_button").performClick()

        assertEquals("ana@citizentech.com" to "Cidadao@2026", enviado)
        composeRule.onNodeWithText("E-mail ou senha inválidos.").assertIsDisplayed()
    }

    @Test
    fun `fluxo de status do admin avanca ate resolvida`() {
        assertEquals("Em análise", ReportStatuses.proximo("Recebida"))
        assertEquals("Resolvida", ReportStatuses.proximo("Encaminhada"))
        assertNull(ReportStatuses.proximo("Resolvida"))
    }
}
