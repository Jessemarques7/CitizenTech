package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.CitizenTechBottomBar
import com.example.ui.components.CitizenTechCard
import com.example.ui.components.CitizenTechNavRail
import com.example.ui.components.CitizenTechTopBar
import com.example.ui.components.Routes
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NewReportScreen
import com.example.ui.screens.PlaceDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.state.CitizenTechUiState
import com.example.ui.state.DataLoadState
import com.example.ui.theme.CitizenTechTheme
import com.example.ui.theme.LocalCitizenTechColors
import com.example.ui.viewmodel.CitizenTechViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CitizenTechViewModel by viewModels {
        val app = application as CitizenTechApplication
        CitizenTechViewModel.provideFactory(app.container.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CitizenTechTheme(
                highContrast = uiState.preferencias.altoContraste,
                userFontScale = uiState.preferencias.fontScale,
            ) {
                if (uiState.precisaLogin) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = LocalCitizenTechColors.current.paper,
                    ) {
                        LoginScreen(
                            apiBaseUrl = uiState.apiConfig.apiBaseUrl,
                            isLoading = uiState.isAuthenticating,
                            errorMessage = uiState.authError,
                            onLogin = viewModel::login,
                            onRegister = viewModel::registrar,
                            onDemoMode = viewModel::entrarModoDemonstracao,
                        )
                    }
                } else {
                    CitizenTechAppShell(
                        uiState = uiState,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun CitizenTechAppShell(
    uiState: CitizenTechUiState,
    viewModel: CitizenTechViewModel,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.EXPLORAR

    val activeNavTab = when {
        currentRoute.startsWith("detalhe") -> Routes.EXPLORAR
        currentRoute.startsWith("novo_relato") -> Routes.RELATOS
        else -> currentRoute
    }

    val colors = LocalCitizenTechColors.current

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short,
            )
            viewModel.consumeSnackbar()
        }
    }

    LaunchedEffect(uiState.reportSubmitSuccessEvent) {
        if (uiState.reportSubmitSuccessEvent != null) {
            viewModel.consumeReportSuccessEvent()
            navController.navigate(Routes.RELATOS) {
                popUpTo(Routes.EXPLORAR) { saveState = true }
                launchSingleTop = true
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colors.paper,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CitizenTechTopBar(
                    isApiMode = uiState.apiConfig.useApiMode,
                    onBrandClick = { navigateTopLevel(Routes.EXPLORAR) },
                    onProfileClick = { navigateTopLevel(Routes.PERFIL) },
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    CitizenTechBottomBar(
                        currentNavRoute = activeNavTab,
                        alertsCount = uiState.alertas.size,
                        onNavigate = ::navigateTopLevel,
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = colors.navy,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useNavRail) {
                    CitizenTechNavRail(
                        currentNavRoute = activeNavTab,
                        alertsCount = uiState.alertas.size,
                        onNavigate = ::navigateTopLevel,
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    when (val loadState = uiState.loadState) {
                        DataLoadState.Loading -> {
                            LoadingStateView()
                        }

                        is DataLoadState.Error -> {
                            ErrorStateView(
                                message = loadState.message,
                                onRetry = { viewModel.refreshAll() },
                                onSwitchToDemo = {
                                    viewModel.updateApiConnection(
                                        useApiMode = false,
                                        baseUrl = uiState.apiConfig.apiBaseUrl,
                                        bearerToken = uiState.apiConfig.bearerToken,
                                    )
                                },
                                showDemoFallback = uiState.apiConfig.useApiMode,
                            )
                        }

                        DataLoadState.Ready -> {
                            NavHost(
                                navController = navController,
                                startDestination = Routes.EXPLORAR,
                            ) {
                                composable(Routes.EXPLORAR) {
                                    ExploreScreen(
                                        uiState = uiState,
                                        onSearchChange = viewModel::updateSearchQuery,
                                        onFilterSelect = viewModel::selectPlaceFilter,
                                        onPlaceClick = { localId ->
                                            viewModel.openLocalDetail(localId)
                                            navController.navigate(Routes.detalheLocal(localId))
                                        },
                                        onAlertsClick = { navigateTopLevel(Routes.ALERTAS) },
                                        onNewReportClick = {
                                            navController.navigate(Routes.novoRelato())
                                        },
                                    )
                                }

                                composable(
                                    route = Routes.DETALHE_LOCAL,
                                    arguments = listOf(
                                        navArgument("localId") { type = NavType.StringType }
                                    ),
                                ) { backStackEntry ->
                                    val localId = backStackEntry.arguments?.getString("localId").orEmpty()
                                    val local = uiState.selectedLocalDetail?.takeIf { it.id == localId }
                                        ?: uiState.locais.find { it.id == localId }
                                    PlaceDetailScreen(
                                        local = local,
                                        relatedReports = uiState.ocorrencias.filter { it.localId == localId },
                                        preferencias = uiState.preferencias,
                                        isLoading = uiState.isDetailLoading,
                                        onBack = { navController.popBackStack() },
                                        onReportBarrier = { id ->
                                            navController.navigate(Routes.novoRelato(id))
                                        },
                                    )
                                }

                                composable(
                                    route = Routes.NOVO_RELATO,
                                    arguments = listOf(
                                        navArgument("localId") {
                                            type = NavType.StringType
                                            nullable = true
                                            defaultValue = null
                                        }
                                    ),
                                ) { backStackEntry ->
                                    val preselected = backStackEntry.arguments?.getString("localId")
                                    NewReportScreen(
                                        locais = uiState.locais,
                                        preselectedLocalId = preselected,
                                        isSubmitting = uiState.isSubmittingReport,
                                        onBack = { navController.popBackStack() },
                                        onSubmit = viewModel::submitReport,
                                    )
                                }

                                composable(Routes.RELATOS) {
                                    ReportsScreen(
                                        uiState = uiState,
                                        onFilterSelect = viewModel::selectReportFilter,
                                        onNewReportClick = {
                                            navController.navigate(Routes.novoRelato())
                                        },
                                        onDeleteReport = viewModel::deleteReport,
                                        isAdmin = uiState.apiConfig.useApiMode && uiState.apiConfig.isAdmin,
                                        onAdvanceStatus = viewModel::avancarStatus,
                                    )
                                }

                                composable(Routes.ALERTAS) {
                                    AlertsScreen(
                                        uiState = uiState,
                                        onSimulateDefaultSensors = viewModel::simulateDefaultSensors,
                                        onSimulateCustomSensors = viewModel::simulateSensorReadings,
                                    )
                                }

                                composable(Routes.PERFIL) {
                                    ProfileScreen(
                                        uiState = uiState,
                                        onToggleEvitarEscadas = viewModel::toggleEvitarEscadas,
                                        onTogglePriorizarRampas = viewModel::togglePriorizarRampas,
                                        onToggleAlertasVisuais = viewModel::toggleAlertasVisuais,
                                        onToggleAltoContraste = viewModel::toggleAltoContraste,
                                        onFontDown = viewModel::decreaseFontScale,
                                        onFontUp = viewModel::increaseFontScale,
                                        onSavePreferences = viewModel::savePreferences,
                                        onUpdateApiConnection = viewModel::updateApiConnection,
                                        onResetDemoData = viewModel::resetDemoData,
                                        onLogout = viewModel::logout,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingStateView() {
    val colors = LocalCitizenTechColors.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = colors.teal)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Preparando sua cidade...",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.muted,
        )
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    onSwitchToDemo: () -> Unit,
    showDemoFallback: Boolean,
) {
    val colors = LocalCitizenTechColors.current
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        CitizenTechCard {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = colors.amber,
                    modifier = Modifier.size(36.dp),
                )
                Text(
                    text = "Não foi possível carregar os dados",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.navy,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.teal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Tentar novamente", fontWeight = FontWeight.Bold)
                }
                if (showDemoFallback) {
                    Button(
                        onClick = onSwitchToDemo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.mint,
                            contentColor = colors.tealDark,
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Voltar para Modo Demonstração", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
