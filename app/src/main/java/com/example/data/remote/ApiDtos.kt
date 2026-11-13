package com.example.data.remote

import com.example.domain.model.Alerta
import com.example.domain.model.LeituraSensorPayload
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.Ocorrencia
import com.example.domain.model.PreferenciasUsuario
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalDto(
    val id: String,
    val nome: String,
    val categoria: String,
    val endereco: String,
    val statusAcessibilidade: String,
    val resumo: String,
    val recursos: List<String> = emptyList(),
    val obstaculos: List<String> = emptyList(),
    val ocupacao: Int = 0,
    val nivelOcupacao: String = "Baixa",
    val ultimaLeitura: String = "",
    val atualizadoEm: String = "",
    val confianca: String = "Cadastro local",
) {
    fun toDomain() = LocalAcessivel(
        id = id,
        nome = nome,
        categoria = categoria,
        endereco = endereco,
        statusAcessibilidade = statusAcessibilidade,
        resumo = resumo,
        recursos = recursos,
        obstaculos = obstaculos,
        ocupacao = ocupacao,
        nivelOcupacao = nivelOcupacao,
        ultimaLeitura = ultimaLeitura,
        atualizadoEm = atualizadoEm,
        confianca = confianca,
    )
}

@JsonClass(generateAdapter = true)
data class OcorrenciaDto(
    val id: String,
    val localId: String,
    val localNome: String = "",
    val categoria: String,
    val descricao: String,
    val status: String = "Recebida",
    val criadoEm: String = "Agora",
    val progresso: Int = 15,
) {
    fun toDomain() = Ocorrencia(
        id = id,
        localId = localId,
        localNome = localNome,
        categoria = categoria,
        descricao = descricao,
        status = status,
        criadoEm = criadoEm,
        progresso = progresso,
    )
}

@JsonClass(generateAdapter = true)
data class CriarOcorrenciaRequest(
    val usuarioId: String,
    val localId: String,
    val categoria: String,
    val descricao: String,
)

@JsonClass(generateAdapter = true)
data class AtualizarStatusRequest(
    val status: String,
)

@JsonClass(generateAdapter = true)
data class AlertaDto(
    val id: String,
    val tipo: String = "Alerta local",
    val titulo: String,
    val local: String,
    val valor: Double = 0.0,
    val unidade: String = "",
    val atualizadoEm: String = "",
    val descricao: String = "",
) {
    fun toDomain() = Alerta(
        id = id,
        tipo = tipo,
        titulo = titulo,
        local = local,
        valor = valor,
        unidade = unidade,
        atualizadoEm = atualizadoEm,
        descricao = descricao,
    )
}

@JsonClass(generateAdapter = true)
data class PreferenciasDto(
    val evitarEscadas: Boolean = true,
    val priorizarRampas: Boolean = true,
    val alertasVisuais: Boolean = true,
    val altoContraste: Boolean = false,
    val usuarioId: String? = null,
) {
    fun toDomain(currentScale: Float, fallbackUserId: String) = PreferenciasUsuario(
        evitarEscadas = evitarEscadas,
        priorizarRampas = priorizarRampas,
        alertasVisuais = alertasVisuais,
        altoContraste = altoContraste,
        fontScale = currentScale,
        usuarioId = usuarioId ?: fallbackUserId,
    )
}

@JsonClass(generateAdapter = true)
data class LeituraSensorRequest(
    val tipo: String,
    val localId: String,
    val localNome: String? = null,
    val valor: Double,
    val unidade: String,
) {
    companion object {
        fun fromDomain(p: LeituraSensorPayload) = LeituraSensorRequest(
            tipo = p.tipo,
            localId = p.localId,
            localNome = p.localNome,
            valor = p.valor,
            unidade = p.unidade,
        )
    }
}

@JsonClass(generateAdapter = true)
data class LeiturasResponseDto(
    val message: String? = null,
    val leituras: Int? = null,
)

@JsonClass(generateAdapter = true)
data class ApiErrorDto(
    val message: String? = null,
    val detail: String? = null,
)
