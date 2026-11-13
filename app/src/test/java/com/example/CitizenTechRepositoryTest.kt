package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CitizenTechDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.DefaultCitizenTechRepository
import com.example.domain.model.ConexaoApiConfig
import com.example.domain.model.LeituraSensorPayload
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CitizenTechRepositoryTest {

    private lateinit var database: CitizenTechDatabase
    private lateinit var repository: DefaultCitizenTechRepository
    private val demoConfig = ConexaoApiConfig(useApiMode = false)

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CitizenTechDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefsStore = UserPreferencesDataStore(context)
        repository = DefaultCitizenTechRepository(database.dao(), prefsStore)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `seed data populates three places, two reports, and one alert`() = runTest {
        val snapshot = repository.loadAllData(demoConfig)
        assertEquals(3, snapshot.locais.size)
        assertEquals(2, snapshot.ocorrencias.size)
        assertEquals(1, snapshot.alertas.size)
        assertEquals("Biblioteca Central", snapshot.locais.first().nome)
    }

    @Test
    fun `creating report persists in Room with status Recebida`() = runTest {
        repository.ensureDemoSeeded()
        val created = repository.criarOcorrencia(
            localId = "terminal",
            categoria = "Rampa bloqueada",
            descricao = "Rampa de acesso bloqueada por cones na entrada principal.",
            fotoUriLocal = null,
            config = demoConfig,
        )
        assertEquals("Recebida", created.status)
        assertEquals("Terminal Central", created.localNome)

        val allReports = repository.demoOcorrenciasFlow.first()
        assertEquals(3, allReports.size)
        assertTrue(allReports.any { it.id == created.id })
    }

    @Test
    fun `sensor simulation updates occupancy level and creates rain alert above threshold`() = runTest {
        repository.ensureDemoSeeded()
        repository.registrarLeituras(
            leituras = listOf(
                LeituraSensorPayload(
                    tipo = "ocupacao",
                    localId = "biblioteca",
                    valor = 12.0,
                    unidade = "pessoas",
                ),
                LeituraSensorPayload(
                    tipo = "chuva",
                    localId = "centro-cultural",
                    localNome = "Praça das Artes",
                    valor = 45.0,
                    unidade = "mm/h",
                ),
            ),
            config = demoConfig,
        )

        val biblioteca = repository.getLocalDetail("biblioteca", demoConfig)
        assertEquals(12, biblioteca.ocupacao)
        assertEquals("Baixa", biblioteca.nivelOcupacao)

        val alerts = repository.demoAlertasFlow.first()
        assertEquals(2, alerts.size)
        assertEquals(45.0, alerts.first().valor, 0.01)
    }
}
