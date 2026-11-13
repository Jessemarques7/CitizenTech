package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LocalEntity::class, OcorrenciaEntity::class, AlertaEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(StringListConverter::class)
abstract class CitizenTechDatabase : RoomDatabase() {
    abstract fun dao(): CitizenTechDao

    companion object {
        @Volatile
        private var INSTANCE: CitizenTechDatabase? = null

        fun getInstance(context: Context): CitizenTechDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CitizenTechDatabase::class.java,
                    "citizentech_demo.db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}

object DemoSeedData {
    fun locais(): List<LocalEntity> = listOf(
        LocalEntity(
            id = "biblioteca",
            nome = "Biblioteca Central",
            categoria = "Serviço público",
            endereco = "Rua das Palmeiras, 120 · Centro",
            statusAcessibilidade = "Parcial",
            resumo = "Entrada principal nivelada. Há um trecho de calçada irregular na lateral.",
            recursos = listOf(
                "Entrada principal com rampa",
                "Banheiro acessível",
                "Balcão de atendimento rebaixado",
                "Piso tátil na entrada",
            ),
            obstaculos = listOf("Calçada lateral irregular"),
            ocupacao = 42,
            nivelOcupacao = "Alta",
            ultimaLeitura = "14:32",
            atualizadoEm = "Hoje, 14:32",
            confianca = "Cadastro + relato comunitário",
            sortOrder = 0,
        ),
        LocalEntity(
            id = "terminal",
            nome = "Terminal Central",
            categoria = "Transporte",
            endereco = "Av. Integração, 450 · Centro",
            statusAcessibilidade = "Acessível",
            resumo = "Acesso principal com piso tátil e elevador sinalizado.",
            recursos = listOf(
                "Rampas de acesso",
                "Piso tátil direcional",
                "Elevador sinalizado",
                "Balcão preferencial",
            ),
            obstaculos = emptyList(),
            ocupacao = 18,
            nivelOcupacao = "Moderada",
            ultimaLeitura = "14:28",
            atualizadoEm = "Hoje, 14:28",
            confianca = "Cadastro local",
            sortOrder = 1,
        ),
        LocalEntity(
            id = "centro-cultural",
            nome = "Centro Cultural",
            categoria = "Cultura e lazer",
            endereco = "Praça das Artes, 35 · Centro",
            statusAcessibilidade = "Acessível",
            resumo = "Entrada sem degraus e espaços de circulação amplos.",
            recursos = listOf(
                "Acesso sem degraus",
                "Banheiro acessível",
                "Assentos reservados",
                "Sinalização visual",
            ),
            obstaculos = emptyList(),
            ocupacao = 9,
            nivelOcupacao = "Baixa",
            ultimaLeitura = "14:25",
            atualizadoEm = "Hoje, 14:25",
            confianca = "Cadastro local",
            sortOrder = 2,
        ),
    )

    fun ocorrencias(): List<OcorrenciaEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            OcorrenciaEntity(
                id = "CT-1024",
                localId = "biblioteca",
                localNome = "Biblioteca Central",
                categoria = "Calçada irregular",
                descricao = "Piso desnivelado na lateral da entrada, próximo à faixa de pedestres.",
                status = "Em análise",
                criadoEm = "Hoje, 10:42",
                progresso = 45,
                createdAtMillis = now,
            ),
            OcorrenciaEntity(
                id = "CT-1018",
                localId = "terminal",
                localNome = "Terminal Central",
                categoria = "Elevador",
                descricao = "Elevador voltou a operar após manutenção.",
                status = "Resolvida",
                criadoEm = "Ontem, 16:10",
                progresso = 100,
                createdAtMillis = now - 86_400_000L,
            ),
        )
    }

    fun alertas(): List<AlertaEntity> = listOf(
        AlertaEntity(
            id = "AL-205",
            tipo = "Clima",
            titulo = "Chuva intensa no entorno da Praça Central",
            local = "Praça Central",
            valor = 35.0,
            unidade = "mm/h",
            atualizadoEm = "Hoje, 14:35",
            descricao = "A leitura simulada ultrapassou o limite de demonstração configurado pela equipe. Considere verificar as condições do trajeto antes de sair.",
            createdAtMillis = System.currentTimeMillis(),
        ),
    )
}
