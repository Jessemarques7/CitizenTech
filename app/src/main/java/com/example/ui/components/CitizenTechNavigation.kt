package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.LocalCitizenTechColors

object Routes {
    const val EXPLORAR = "explorar"
    const val DETALHE_LOCAL = "detalhe/{localId}"
    const val NOVO_RELATO = "novo_relato?localId={localId}"
    const val RELATOS = "relatos"
    const val ALERTAS = "alertas"
    const val PERFIL = "perfil"

    fun detalheLocal(localId: String) = "detalhe/$localId"
    fun novoRelato(localId: String? = null) =
        if (localId.isNullOrBlank()) "novo_relato" else "novo_relato?localId=$localId"
}

data class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
)

val topLevelDestinations = listOf(
    TopLevelDestination(
        route = Routes.EXPLORAR,
        label = "Explorar",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_explorar",
    ),
    TopLevelDestination(
        route = Routes.RELATOS,
        label = "Relatos",
        selectedIcon = Icons.AutoMirrored.Filled.Assignment,
        unselectedIcon = Icons.AutoMirrored.Outlined.Assignment,
        testTag = "nav_relatos",
    ),
    TopLevelDestination(
        route = Routes.ALERTAS,
        label = "Alertas",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        testTag = "nav_alertas",
    ),
    TopLevelDestination(
        route = Routes.PERFIL,
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_perfil",
    ),
)

@Composable
fun CitizenTechBottomBar(
    currentNavRoute: String,
    alertsCount: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        HorizontalDivider(thickness = 1.dp, color = colors.border)
        NavigationBar(
            containerColor = colors.cardSurface,
            windowInsets = WindowInsets.navigationBars,
        ) {
            topLevelDestinations.forEach { dest ->
                val selected = currentNavRoute == dest.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(dest.route) },
                    modifier = Modifier.testTag(dest.testTag),
                    icon = {
                        val icon = if (selected) dest.selectedIcon else dest.unselectedIcon
                        if (dest.route == Routes.ALERTAS && alertsCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = AmberAccent) {
                                        Text(
                                            text = alertsCount.coerceAtMost(9).toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                },
                            ) {
                                Icon(imageVector = icon, contentDescription = dest.label)
                            }
                        } else {
                            Icon(imageVector = icon, contentDescription = dest.label)
                        }
                    },
                    label = {
                        Text(
                            text = dest.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.tealDark,
                        selectedTextColor = colors.tealDark,
                        indicatorColor = colors.mint,
                        unselectedIconColor = colors.muted,
                        unselectedTextColor = colors.muted,
                    ),
                )
            }
        }
    }
}

@Composable
fun CitizenTechNavRail(
    currentNavRoute: String,
    alertsCount: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalCitizenTechColors.current

    NavigationRail(
        modifier = modifier,
        containerColor = colors.cardSurface,
    ) {
        topLevelDestinations.forEach { dest ->
            val selected = currentNavRoute == dest.route
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(dest.route) },
                modifier = Modifier.testTag("${dest.testTag}_rail"),
                icon = {
                    val icon = if (selected) dest.selectedIcon else dest.unselectedIcon
                    if (dest.route == Routes.ALERTAS && alertsCount > 0) {
                        BadgedBox(
                            badge = { Badge(containerColor = AmberAccent) }
                        ) {
                            Icon(imageVector = icon, contentDescription = dest.label)
                        }
                    } else {
                        Icon(imageVector = icon, contentDescription = dest.label)
                    }
                },
                label = {
                    Text(
                        text = dest.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = colors.tealDark,
                    selectedTextColor = colors.tealDark,
                    indicatorColor = colors.mint,
                    unselectedIconColor = colors.muted,
                    unselectedTextColor = colors.muted,
                ),
            )
        }
    }
}
