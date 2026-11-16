package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenTechDao {

    @Query("SELECT * FROM locais ORDER BY sortOrder ASC")
    fun observeLocais(): Flow<List<LocalEntity>>

    @Query("SELECT * FROM locais ORDER BY sortOrder ASC")
    suspend fun getLocaisOnce(): List<LocalEntity>

    @Query("SELECT * FROM locais WHERE id = :id LIMIT 1")
    suspend fun getLocalById(id: String): LocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocais(locais: List<LocalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocal(local: LocalEntity)

    @Query("DELETE FROM locais")
    suspend fun clearLocais()

    @Query("SELECT * FROM ocorrencias ORDER BY createdAtMillis DESC")
    fun observeOcorrencias(): Flow<List<OcorrenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcorrencias(ocorrencias: List<OcorrenciaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOcorrencia(ocorrencia: OcorrenciaEntity)

    @Query("DELETE FROM ocorrencias WHERE id = :id")
    suspend fun deleteOcorrenciaById(id: String)

    @Query("SELECT * FROM ocorrencias WHERE id = :id LIMIT 1")
    suspend fun getOcorrenciaById(id: String): OcorrenciaEntity?

    @Query("DELETE FROM ocorrencias")
    suspend fun clearOcorrencias()

    @Query("SELECT * FROM alertas ORDER BY createdAtMillis DESC")
    fun observeAlertas(): Flow<List<AlertaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertas(alertas: List<AlertaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerta(alerta: AlertaEntity)

    @Query("DELETE FROM alertas")
    suspend fun clearAlertas()
}
