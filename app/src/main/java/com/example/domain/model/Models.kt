package com.example.domain.model

data class LocalAcessivel(
    val id: String,
    val nome: String,
    val categoria: String,
    val endereco: String,
    val statusAcessibilidade: String,
    val resumo: String,
    val recursos: List<String>,
    val obstaculos: List<String>,
    val ocupacao: Int,
    val nivelOcupacao: String,
    val ultimaLeitura: String,
    val atualizadoEm: String,
    val confianca: String,
)

data class Ocorrencia(
    val id: String,
    val localId: String,
    val localNome: String,
    val categoria: String,
    val descricao: String,
    val status: String,
    val criadoEm: String,
    val progresso: Int,
    val fotoUriLocal: String? = null,
)

data class Alerta(
    val id: String,
    val tipo: String,
    val titulo: String,
    val local: String,
    val valor: Double,
    val unidade: String,
    val atualizadoEm: String,
    val descricao: String,
)

data class PreferenciasUsuario(
    val evitarEscadas: Boolean = true,
    val priorizarRampas: Boolean = true,
    val alertasVisuais: Boolean = true,
    val altoContraste: Boolean = false,
    val fontScale: Float = 1.0f,
    val usuarioId: String = DEFAULT_USER_ID,
) {
    companion object {
        const val DEFAULT_USER_ID = "cidadao-demo-01"
    }
}

data class ConexaoApiConfig(
    val useApiMode: Boolean = false,
    val apiBaseUrl: String = DEFAULT_API_BASE_URL,
    val bearerToken: String = "",
) {
    companion object {
        const val DEFAULT_API_BASE_URL = "http://10.0.2.2:8080/api/"
    }
}

data class LeituraSensorPayload(
    val tipo: String,
    val localId: String,
    val localNome: String? = null,
    val valor: Double,
    val unidade: String,
)

object BarrierCategories {
    val all = listOf(
        "Calçada irregular",
        "Rampa bloqueada",
        "Ausência de rampa",
        "Escada sem alternativa",
        "Elevador fora de serviço",
        "Travessia difícil",
        "Piso tátil danificado",
        "Sinalização inadequada",
        "Outro",
    )
}

object ReportStatuses {
    val filters = listOf("Todos", "Recebida", "Em análise", "Encaminhada", "Resolvida")
}

object AccessibilityFilters {
    const val ALL = "Todos"
    const val ACCESSIBLE = "Acessível"
    const val PARTIAL = "Parcial"
    val all = listOf(ALL, ACCESSIBLE, PARTIAL)
}
