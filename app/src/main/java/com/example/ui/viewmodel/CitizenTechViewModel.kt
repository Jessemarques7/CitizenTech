package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CitizenTechRepository
import com.example.domain.model.ConexaoApiConfig
import com.example.domain.model.LeituraSensorPayload
import com.example.domain.model.PreferenciasUsuario
import com.example.ui.state.CitizenTechUiState
import com.example.ui.state.DataLoadState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CitizenTechViewModel(
    private val repository: CitizenTechRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenTechUiState())
    val uiState: StateFlow<CitizenTechUiState> = _uiState.asStateFlow()

    private var demoObserverJob: Job? = null

    init {
        observePreferencesAndConfig()
        refreshAll(showInitialBanner = true)
    }

    private fun observePreferencesAndConfig() {
        viewModelScope.launch {
            combine(
                repository.preferenciasFlow,
                repository.apiConfigFlow,
            ) { prefs, apiConfig -> prefs to apiConfig }
                .collectLatest { (prefs, apiConfig) ->
                    _uiState.update { state ->
                        state.copy(
                            preferencias = prefs,
                            apiConfig = apiConfig,
                        )
                    }
                    if (!apiConfig.useApiMode) {
                        startDemoObservers()
                    } else {
                        demoObserverJob?.cancel()
                    }
                }
        }
    }

    private fun startDemoObservers() {
        if (demoObserverJob?.isActive == true) return
        demoObserverJob = viewModelScope.launch {
            repository.ensureDemoSeeded()
            combine(
                repository.demoLocaisFlow,
                repository.demoOcorrenciasFlow,
                repository.demoAlertasFlow,
            ) { locais, ocorrencias, alertas ->
                Triple(locais, ocorrencias, alertas)
            }.collectLatest { (locais, ocorrencias, alertas) ->
                _uiState.update { current ->
                    if (current.apiConfig.useApiMode) return@update current
                    val updatedDetail = current.selectedLocalDetail?.let { sel ->
                        locais.find { it.id == sel.id } ?: sel
                    }
                    current.copy(
                        loadState = DataLoadState.Ready,
                        locais = locais,
                        ocorrencias = ocorrencias,
                        alertas = alertas,
                        selectedLocalDetail = updatedDetail,
                    )
                }
            }
        }
    }

    fun refreshAll(showInitialBanner: Boolean = false) {
        viewModelScope.launch {
            val config = repository.apiConfigFlow.first()
            _uiState.update {
                if (it.locais.isEmpty()) it.copy(loadState = DataLoadState.Loading) else it
            }
            try {
                val snapshot = repository.loadAllData(config)
                _uiState.update { state ->
                    val updatedDetail = state.selectedLocalDetail?.let { sel ->
                        snapshot.locais.find { it.id == sel.id } ?: sel
                    }
                    state.copy(
                        loadState = DataLoadState.Ready,
                        locais = snapshot.locais,
                        ocorrencias = snapshot.ocorrencias,
                        alertas = snapshot.alertas,
                        preferencias = snapshot.preferencias,
                        selectedLocalDetail = updatedDetail,
                        snackbarMessage = if (showInitialBanner) {
                            if (config.useApiMode) "Conectado à API do grupo"
                            else "Modo demonstração: dados fictícios"
                        } else {
                            state.snackbarMessage
                        },
                    )
                }
            } catch (e: Throwable) {
                _uiState.update { state ->
                    state.copy(
                        loadState = DataLoadState.Error(
                            e.message ?: "Não foi possível carregar os dados."
                        )
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectPlaceFilter(filter: String) {
        _uiState.update { it.copy(selectedPlaceFilter = filter) }
    }

    fun selectReportFilter(filter: String) {
        _uiState.update { it.copy(selectedReportFilter = filter) }
    }

    fun openLocalDetail(localId: String) {
        val cached = _uiState.value.locais.find { it.id == localId }
        _uiState.update {
            it.copy(
                selectedLocalDetail = cached,
                isDetailLoading = cached == null,
            )
        }
        viewModelScope.launch {
            try {
                val detail = repository.getLocalDetail(localId, _uiState.value.apiConfig)
                _uiState.update { state ->
                    val updatedList = state.locais.map { if (it.id == detail.id) detail else it }
                    state.copy(
                        locais = updatedList,
                        selectedLocalDetail = detail,
                        isDetailLoading = false,
                    )
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        snackbarMessage = e.message ?: "Não foi possível carregar os detalhes do local.",
                    )
                }
            }
        }
    }

    fun submitReport(
        localId: String,
        categoria: String,
        descricao: String,
        fotoUriLocal: String?,
    ) {
        val trimmedDesc = descricao.trim()
        if (localId.isBlank()) {
            showSnackbar("Selecione o local da ocorrência.")
            return
        }
        if (categoria.isBlank()) {
            showSnackbar("Selecione a categoria da barreira.")
            return
        }
        if (trimmedDesc.length < 12) {
            showSnackbar("Descreva a barreira com pelo menos 12 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingReport = true) }
            try {
                val config = _uiState.value.apiConfig
                repository.criarOcorrencia(
                    localId = localId,
                    categoria = categoria,
                    descricao = trimmedDesc,
                    fotoUriLocal = fotoUriLocal,
                    config = config,
                )
                if (config.useApiMode) {
                    val snapshot = repository.loadAllData(config)
                    _uiState.update {
                        it.copy(
                            locais = snapshot.locais,
                            ocorrencias = snapshot.ocorrencias,
                            alertas = snapshot.alertas,
                        )
                    }
                }
                _uiState.update {
                    it.copy(
                        isSubmittingReport = false,
                        selectedReportFilter = "Todos",
                        reportSubmitSuccessEvent = System.currentTimeMillis(),
                        snackbarMessage = "Relato enviado e registrado como recebido.",
                    )
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isSubmittingReport = false,
                        snackbarMessage = e.message ?: "Não foi possível enviar o relato.",
                    )
                }
            }
        }
    }

    fun deleteReport(id: String) {
        viewModelScope.launch {
            try {
                val config = _uiState.value.apiConfig
                repository.excluirOcorrencia(id, config)
                if (config.useApiMode) {
                    val snapshot = repository.loadAllData(config)
                    _uiState.update { it.copy(ocorrencias = snapshot.ocorrencias) }
                }
                showSnackbar("Relato $id cancelado.")
            } catch (e: Throwable) {
                showSnackbar(e.message ?: "Erro ao remover relato.")
            }
        }
    }

    fun toggleEvitarEscadas() = updateLocalPrefs { it.copy(evitarEscadas = !it.evitarEscadas) }
    fun togglePriorizarRampas() = updateLocalPrefs { it.copy(priorizarRampas = !it.priorizarRampas) }
    fun toggleAlertasVisuais() = updateLocalPrefs { it.copy(alertasVisuais = !it.alertasVisuais) }
    fun toggleAltoContraste() = updateLocalPrefs { it.copy(altoContraste = !it.altoContraste) }

    fun increaseFontScale() = updateLocalPrefs {
        it.copy(fontScale = ((it.fontScale + 0.05f).coerceAtMost(1.25f)))
    }

    fun decreaseFontScale() = updateLocalPrefs {
        it.copy(fontScale = ((it.fontScale - 0.05f).coerceAtLeast(0.90f)))
    }

    private fun updateLocalPrefs(transform: (PreferenciasUsuario) -> PreferenciasUsuario) {
        val updated = transform(_uiState.value.preferencias)
        _uiState.update { it.copy(preferencias = updated) }
        viewModelScope.launch {
            repository.saveLocalUiPreferences(updated)
        }
    }

    fun savePreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingPreferences = true) }
            try {
                val saved = repository.salvarPreferencias(
                    prefs = _uiState.value.preferencias,
                    config = _uiState.value.apiConfig,
                )
                _uiState.update {
                    it.copy(
                        isSavingPreferences = false,
                        preferencias = saved,
                        snackbarMessage = "Preferências salvas.",
                    )
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isSavingPreferences = false,
                        snackbarMessage = e.message ?: "Erro ao salvar preferências.",
                    )
                }
            }
        }
    }

    fun simulateDefaultSensors() {
        simulateSensorReadings(
            ocupacaoBiblioteca = 42,
            chuvaPracaArtes = 35.0,
        )
    }

    fun simulateSensorReadings(ocupacaoBiblioteca: Int, chuvaPracaArtes: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSimulatingSensors = true) }
            try {
                val config = _uiState.value.apiConfig
                val payloads = listOf(
                    LeituraSensorPayload(
                        tipo = "ocupacao",
                        localId = "biblioteca",
                        valor = ocupacaoBiblioteca.toDouble(),
                        unidade = "pessoas",
                    ),
                    LeituraSensorPayload(
                        tipo = "chuva",
                        localId = "centro-cultural",
                        localNome = "Praça das Artes",
                        valor = chuvaPracaArtes,
                        unidade = "mm/h",
                    ),
                )
                repository.registrarLeituras(payloads, config)
                if (config.useApiMode) {
                    val snapshot = repository.loadAllData(config)
                    _uiState.update {
                        it.copy(
                            locais = snapshot.locais,
                            ocorrencias = snapshot.ocorrencias,
                            alertas = snapshot.alertas,
                        )
                    }
                }
                _uiState.update {
                    it.copy(
                        isSimulatingSensors = false,
                        snackbarMessage = "Leituras simuladas registradas.",
                    )
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isSimulatingSensors = false,
                        snackbarMessage = e.message ?: "Erro ao registrar leituras simuladas.",
                    )
                }
            }
        }
    }

    fun updateApiConnection(useApiMode: Boolean, baseUrl: String, bearerToken: String) {
        viewModelScope.launch {
            val newConfig = ConexaoApiConfig(
                useApiMode = useApiMode,
                apiBaseUrl = baseUrl.trim().ifEmpty { ConexaoApiConfig.DEFAULT_API_BASE_URL },
                bearerToken = bearerToken.trim(),
            )
            repository.saveApiConfig(newConfig)
            refreshAll(showInitialBanner = true)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            showSnackbar("Dados de demonstração restaurados.")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun consumeReportSuccessEvent() {
        _uiState.update { it.copy(reportSubmitSuccessEvent = null) }
    }

    companion object {
        fun provideFactory(repository: CitizenTechRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CitizenTechViewModel(repository) as T
                }
            }
    }
}
