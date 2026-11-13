package com.example.ui.state

import com.example.domain.model.AccessibilityFilters
import com.example.domain.model.Alerta
import com.example.domain.model.ConexaoApiConfig
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.Ocorrencia
import com.example.domain.model.PreferenciasUsuario
import com.example.domain.model.ReportStatuses

sealed interface DataLoadState {
    data object Loading : DataLoadState
    data object Ready : DataLoadState
    data class Error(val message: String) : DataLoadState
}

data class CitizenTechUiState(
    val loadState: DataLoadState = DataLoadState.Loading,
    val locais: List<LocalAcessivel> = emptyList(),
    val ocorrencias: List<Ocorrencia> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val preferencias: PreferenciasUsuario = PreferenciasUsuario(),
    val apiConfig: ConexaoApiConfig = ConexaoApiConfig(),
    val searchQuery: String = "",
    val selectedPlaceFilter: String = AccessibilityFilters.ALL,
    val selectedReportFilter: String = ReportStatuses.filters.first(),
    val selectedLocalDetail: LocalAcessivel? = null,
    val isDetailLoading: Boolean = false,
    val isSubmittingReport: Boolean = false,
    val isSimulatingSensors: Boolean = false,
    val isSavingPreferences: Boolean = false,
    val reportSubmitSuccessEvent: Long? = null,
    val snackbarMessage: String? = null,
) {
    val filteredLocais: List<LocalAcessivel>
        get() {
            val q = searchQuery.trim().lowercase()
            return locais.filter { local ->
                val matchesStatus = selectedPlaceFilter == AccessibilityFilters.ALL ||
                    local.statusAcessibilidade.equals(selectedPlaceFilter, ignoreCase = true)
                val matchesQuery = q.isEmpty() ||
                    "${local.nome} ${local.endereco} ${local.categoria}".lowercase().contains(q)
                matchesStatus && matchesQuery
            }
        }

    val filteredOcorrencias: List<Ocorrencia>
        get() = ocorrencias.filter { o ->
            selectedReportFilter == "Todos" ||
                o.status.equals(selectedReportFilter, ignoreCase = true)
        }
}
