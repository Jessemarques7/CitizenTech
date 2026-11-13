package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.domain.model.Alerta
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.Ocorrencia

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(separator = "\u001F")

    @TypeConverter
    fun toList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split("\u001F").filter { it.isNotBlank() }
}

@Entity(tableName = "locais")
data class LocalEntity(
    @PrimaryKey val id: String,
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
    val sortOrder: Int = 0,
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

@Entity(tableName = "ocorrencias")
data class OcorrenciaEntity(
    @PrimaryKey val id: String,
    val localId: String,
    val localNome: String,
    val categoria: String,
    val descricao: String,
    val status: String,
    val criadoEm: String,
    val progresso: Int,
    val fotoUriLocal: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
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
        fotoUriLocal = fotoUriLocal,
    )
}

@Entity(tableName = "alertas")
data class AlertaEntity(
    @PrimaryKey val id: String,
    val tipo: String,
    val titulo: String,
    val local: String,
    val valor: Double,
    val unidade: String,
    val atualizadoEm: String,
    val descricao: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
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
