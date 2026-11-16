package com.example.data.repository

import com.example.data.local.AlertaEntity
import com.example.data.local.CitizenTechDao
import com.example.data.local.DemoSeedData
import com.example.data.local.OcorrenciaEntity
import com.example.data.local.UserPreferencesDataStore
import com.example.data.remote.AtualizarStatusRequest
import com.example.data.remote.CitizenTechApiService
import com.example.data.remote.CriarOcorrenciaRequest
import com.example.data.remote.LeituraSensorRequest
import com.example.data.remote.LoginRequest
import com.example.data.remote.NetworkClientFactory
import com.example.data.remote.PreferenciasDto
import com.example.data.remote.RegistroRequest
import com.example.data.remote.TokenResponseDto
import com.example.domain.model.Alerta
import com.example.domain.model.ConexaoApiConfig
import com.example.domain.model.LeituraSensorPayload
import com.example.domain.model.LocalAcessivel
import com.example.domain.model.Ocorrencia
import com.example.domain.model.PreferenciasUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

data class DashboardSnapshot(
    val locais: List<LocalAcessivel>,
    val ocorrencias: List<Ocorrencia>,
    val alertas: List<Alerta>,
    val preferencias: PreferenciasUsuario,
)

interface CitizenTechRepository {
    val demoLocaisFlow: Flow<List<LocalAcessivel>>
    val demoOcorrenciasFlow: Flow<List<Ocorrencia>>
    val demoAlertasFlow: Flow<List<Alerta>>
    val preferenciasFlow: Flow<PreferenciasUsuario>
    val apiConfigFlow: Flow<ConexaoApiConfig>

    suspend fun ensureDemoSeeded()
    suspend fun resetDemoData()
    suspend fun loadAllData(config: ConexaoApiConfig): DashboardSnapshot
    suspend fun getLocalDetail(id: String, config: ConexaoApiConfig): LocalAcessivel
    suspend fun criarOcorrencia(
        localId: String,
        categoria: String,
        descricao: String,
        fotoUriLocal: String?,
        config: ConexaoApiConfig,
    ): Ocorrencia

    suspend fun excluirOcorrencia(id: String, config: ConexaoApiConfig)
    suspend fun salvarPreferencias(prefs: PreferenciasUsuario, config: ConexaoApiConfig): PreferenciasUsuario
    suspend fun saveLocalUiPreferences(prefs: PreferenciasUsuario)
    suspend fun saveApiConfig(config: ConexaoApiConfig)
    suspend fun registrarLeituras(
        leituras: List<LeituraSensorPayload>,
        config: ConexaoApiConfig,
    )

    suspend fun login(baseUrl: String, email: String, senha: String): ConexaoApiConfig
    suspend fun registrar(baseUrl: String, nome: String, email: String, senha: String): ConexaoApiConfig
    suspend fun logout()
    suspend fun atualizarStatus(id: String, status: String, config: ConexaoApiConfig): Ocorrencia
}

class DefaultCitizenTechRepository(
    private val dao: CitizenTechDao,
    private val prefsStore: UserPreferencesDataStore,
) : CitizenTechRepository {

    override val demoLocaisFlow: Flow<List<LocalAcessivel>> =
        dao.observeLocais().map { list -> list.map { it.toDomain() } }

    override val demoOcorrenciasFlow: Flow<List<Ocorrencia>> =
        dao.observeOcorrencias().map { list -> list.map { it.toDomain() } }

    override val demoAlertasFlow: Flow<List<Alerta>> =
        dao.observeAlertas().map { list -> list.map { it.toDomain() } }

    override val preferenciasFlow: Flow<PreferenciasUsuario> = prefsStore.preferencesFlow
    override val apiConfigFlow: Flow<ConexaoApiConfig> = prefsStore.apiConfigFlow

    override suspend fun ensureDemoSeeded() {
        if (dao.getLocaisOnce().isEmpty()) {
            dao.insertLocais(DemoSeedData.locais())
            dao.insertOcorrencias(DemoSeedData.ocorrencias())
            dao.insertAlertas(DemoSeedData.alertas())
        }
    }

    override suspend fun resetDemoData() {
        dao.clearLocais()
        dao.clearOcorrencias()
        dao.clearAlertas()
        dao.insertLocais(DemoSeedData.locais())
        dao.insertOcorrencias(DemoSeedData.ocorrencias())
        dao.insertAlertas(DemoSeedData.alertas())
    }

    override suspend fun loadAllData(config: ConexaoApiConfig): DashboardSnapshot {
        val localPrefs = prefsStore.preferencesFlow.first()
        if (!config.useApiMode) {
            ensureDemoSeeded()
            return DashboardSnapshot(
                locais = dao.observeLocais().first().map { it.toDomain() },
                ocorrencias = dao.observeOcorrencias().first().map { it.toDomain() },
                alertas = dao.observeAlertas().first().map { it.toDomain() },
                preferencias = localPrefs,
            )
        }

        return try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            val locais = api.getLocais().map { it.toDomain() }
            // Admin acompanha todos os relatos; cidadão só os próprios (a API também garante isso).
            val ocorrencias = api.getOcorrencias(if (config.isAdmin) null else localPrefs.usuarioId)
                .map { it.toDomain() }
            val alertas = api.getAlertas().map { it.toDomain() }
            val remotePrefs = runCatching {
                api.getPreferencias(localPrefs.usuarioId)
                    .toDomain(localPrefs.fontScale, localPrefs.usuarioId)
            }.getOrDefault(localPrefs)
            DashboardSnapshot(
                locais = locais,
                ocorrencias = ocorrencias,
                alertas = alertas,
                preferencias = remotePrefs,
            )
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun getLocalDetail(id: String, config: ConexaoApiConfig): LocalAcessivel {
        if (!config.useApiMode) {
            ensureDemoSeeded()
            return dao.getLocalById(id)?.toDomain()
                ?: throw NoSuchElementException("Local não encontrado.")
        }
        return try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            api.getLocal(id).toDomain()
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun criarOcorrencia(
        localId: String,
        categoria: String,
        descricao: String,
        fotoUriLocal: String?,
        config: ConexaoApiConfig,
    ): Ocorrencia {
        val currentPrefs = prefsStore.preferencesFlow.first()
        if (!config.useApiMode) {
            ensureDemoSeeded()
            val local = dao.getLocalById(localId)
                ?: throw IllegalArgumentException("Selecione um local válido.")
            val novoId = "CT-${Random.nextInt(1000, 9999)}"
            val entity = OcorrenciaEntity(
                id = novoId,
                localId = localId,
                localNome = local.nome,
                categoria = categoria,
                descricao = descricao.trim(),
                status = "Recebida",
                criadoEm = "Agora (${currentHourMinute()})",
                progresso = 15,
                fotoUriLocal = fotoUriLocal,
                createdAtMillis = System.currentTimeMillis(),
            )
            dao.insertOcorrencia(entity)
            return entity.toDomain()
        }

        return try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            val created = api.criarOcorrencia(
                CriarOcorrenciaRequest(
                    usuarioId = currentPrefs.usuarioId,
                    localId = localId,
                    categoria = categoria,
                    descricao = descricao.trim(),
                )
            )
            created.toDomain().copy(fotoUriLocal = fotoUriLocal)
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun excluirOcorrencia(id: String, config: ConexaoApiConfig) {
        if (!config.useApiMode) {
            dao.deleteOcorrenciaById(id)
            return
        }
        try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            val response = api.excluirOcorrencia(id)
            if (!response.isSuccessful) {
                throw IllegalStateException("A API respondeu com status ${response.code()} ao remover ocorrência.")
            }
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun salvarPreferencias(
        prefs: PreferenciasUsuario,
        config: ConexaoApiConfig,
    ): PreferenciasUsuario {
        prefsStore.savePreferences(prefs)
        if (!config.useApiMode) return prefs

        return try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            val updated = api.salvarPreferencias(
                usuarioId = prefs.usuarioId,
                request = PreferenciasDto(
                    evitarEscadas = prefs.evitarEscadas,
                    priorizarRampas = prefs.priorizarRampas,
                    alertasVisuais = prefs.alertasVisuais,
                    altoContraste = prefs.altoContraste,
                    usuarioId = prefs.usuarioId,
                ),
            )
            updated.toDomain(prefs.fontScale, prefs.usuarioId)
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun saveLocalUiPreferences(prefs: PreferenciasUsuario) {
        prefsStore.savePreferences(prefs)
    }

    override suspend fun saveApiConfig(config: ConexaoApiConfig) {
        prefsStore.saveApiConfig(config)
    }

    override suspend fun registrarLeituras(
        leituras: List<LeituraSensorPayload>,
        config: ConexaoApiConfig,
    ) {
        if (!config.useApiMode) {
            ensureDemoSeeded()
            val timeNow = currentHourMinute()
            leituras.forEach { reading ->
                if (reading.tipo == "ocupacao") {
                    val existing = dao.getLocalById(reading.localId)
                    if (existing != null) {
                        val occ = reading.valor.roundToInt().coerceAtLeast(0)
                        val level = when {
                            occ >= 35 -> "Alta"
                            occ >= 16 -> "Moderada"
                            else -> "Baixa"
                        }
                        dao.insertLocal(
                            existing.copy(
                                ocupacao = occ,
                                nivelOcupacao = level,
                                ultimaLeitura = timeNow,
                                atualizadoEm = "Agora ($timeNow)",
                            )
                        )
                    }
                }
                if (reading.tipo == "chuva" && reading.valor >= 30.0) {
                    val alert = AlertaEntity(
                        id = "AL-${System.currentTimeMillis()}",
                        tipo = "Clima",
                        titulo = "Chuva intensa identificada na área monitorada",
                        local = reading.localNome ?: "Ponto monitorado",
                        valor = reading.valor,
                        unidade = reading.unidade,
                        atualizadoEm = "Agora ($timeNow)",
                        descricao = "A leitura do sensor simulado ultrapassou o limite de demonstração configurado pela equipe (30 mm/h).",
                        createdAtMillis = System.currentTimeMillis(),
                    )
                    dao.insertAlerta(alert)
                }
            }
            return
        }

        try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            api.registrarLeituras(leituras.map { LeituraSensorRequest.fromDomain(it) })
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    override suspend fun login(baseUrl: String, email: String, senha: String): ConexaoApiConfig =
        autenticar(baseUrl) { api -> api.login(LoginRequest(email.trim(), senha)) }

    override suspend fun registrar(baseUrl: String, nome: String, email: String, senha: String): ConexaoApiConfig =
        autenticar(baseUrl) { api -> api.registrar(RegistroRequest(nome.trim(), email.trim(), senha)) }

    /** Chama login/cadastro sem token, guarda o JWT e os dados do usuário na sessão. */
    private suspend fun autenticar(
        baseUrl: String,
        chamada: suspend (CitizenTechApiService) -> TokenResponseDto,
    ): ConexaoApiConfig {
        val resposta = try {
            chamada(NetworkClientFactory.createApiService(baseUrl, bearerToken = ""))
        } catch (e: Throwable) {
            // Aqui 401 significa senha errada, não sessão expirada: mostra a mensagem da API.
            throw IllegalStateException(NetworkClientFactory.parseFriendlyError(e), e)
        }
        val config = ConexaoApiConfig(
            useApiMode = true,
            apiBaseUrl = NetworkClientFactory.normalizeBaseUrl(baseUrl),
            bearerToken = resposta.token,
            usuarioNome = resposta.usuario.nome,
            usuarioEmail = resposta.usuario.email,
            perfil = resposta.usuario.perfil,
        )
        prefsStore.saveApiConfig(config)
        prefsStore.savePreferences(prefsStore.preferencesFlow.first().copy(usuarioId = resposta.usuario.id))
        return config
    }

    override suspend fun logout() {
        prefsStore.saveApiConfig(prefsStore.apiConfigFlow.first().semSessao())
    }

    override suspend fun atualizarStatus(id: String, status: String, config: ConexaoApiConfig): Ocorrencia {
        if (!config.useApiMode) {
            val atual = dao.getOcorrenciaById(id) ?: throw NoSuchElementException("Relato não encontrado.")
            val progresso = when (status) {
                "Em análise" -> 45
                "Encaminhada" -> 75
                "Resolvida" -> 100
                else -> 15
            }
            val atualizada = atual.copy(status = status, progresso = progresso)
            dao.insertOcorrencia(atualizada)
            return atualizada.toDomain()
        }
        return try {
            val api = NetworkClientFactory.createApiService(config.apiBaseUrl, config.bearerToken)
            api.atualizarStatus(id, AtualizarStatusRequest(status)).toDomain()
        } catch (e: Throwable) {
            throw NetworkClientFactory.toAppError(e)
        }
    }

    private fun currentHourMinute(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}
