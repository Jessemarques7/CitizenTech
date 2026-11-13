package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CitizenTechUiRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `explore screen loads places and navigates to detail and tabs`() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Olá, Alex.").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Olá, Alex.").assertIsDisplayed()

        // Scroll to search input and filter list
        composeRule.onNodeWithTag("explore_list")
            .performScrollToNode(hasTestTag("explore_search_input"))
        composeRule.onNodeWithTag("explore_search_input").performTextInput("Terminal")
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("explore_list")
            .performScrollToNode(hasTestTag("place_card_terminal"))
        composeRule.onNodeWithText("Terminal Central").assertIsDisplayed()

        // Clear search and navigate to Biblioteca Central detail
        composeRule.onNodeWithTag("explore_list")
            .performScrollToNode(hasTestTag("explore_clear_search"))
        composeRule.onNodeWithTag("explore_clear_search").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("explore_list")
            .performScrollToNode(hasTestTag("place_card_biblioteca"))
        composeRule.onNodeWithTag("place_card_biblioteca").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Biblioteca Central").assertIsDisplayed()

        // Navigate back and visit Relatos, Alertas, Perfil tabs
        composeRule.onNodeWithTag("detail_back_button").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("nav_relatos").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Meus relatos").assertIsDisplayed()

        composeRule.onNodeWithTag("nav_alertas").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Alertas da região").assertIsDisplayed()

        composeRule.onNodeWithTag("nav_perfil").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Seu jeito de circular").assertIsDisplayed()
    }
}
