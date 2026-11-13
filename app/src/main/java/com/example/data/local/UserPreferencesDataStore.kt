package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.ConexaoApiConfig
import com.example.domain.model.PreferenciasUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "citizentech_prefs")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val EVITAR_ESCADAS = booleanPreferencesKey("evitar_escadas")
        val PRIORIZAR_RAMPAS = booleanPreferencesKey("priorizar_rampas")
        val ALERTAS_VISUAIS = booleanPreferencesKey("alertas_visuais")
        val ALTO_CONTRASTE = booleanPreferencesKey("alto_contraste")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val USUARIO_ID = stringPreferencesKey("usuario_id")
        val USE_API_MODE = booleanPreferencesKey("use_api_mode")
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val BEARER_TOKEN = stringPreferencesKey("bearer_token")
    }

    val preferencesFlow: Flow<PreferenciasUsuario> = context.dataStore.data.map { prefs ->
        PreferenciasUsuario(
            evitarEscadas = prefs[Keys.EVITAR_ESCADAS] ?: true,
            priorizarRampas = prefs[Keys.PRIORIZAR_RAMPAS] ?: true,
            alertasVisuais = prefs[Keys.ALERTAS_VISUAIS] ?: true,
            altoContraste = prefs[Keys.ALTO_CONTRASTE] ?: false,
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            usuarioId = prefs[Keys.USUARIO_ID] ?: PreferenciasUsuario.DEFAULT_USER_ID,
        )
    }

    val apiConfigFlow: Flow<ConexaoApiConfig> = context.dataStore.data.map { prefs ->
        ConexaoApiConfig(
            useApiMode = prefs[Keys.USE_API_MODE] ?: false,
            apiBaseUrl = prefs[Keys.API_BASE_URL] ?: ConexaoApiConfig.DEFAULT_API_BASE_URL,
            bearerToken = prefs[Keys.BEARER_TOKEN] ?: "",
        )
    }

    suspend fun savePreferences(prefs: PreferenciasUsuario) {
        context.dataStore.edit { store ->
            store[Keys.EVITAR_ESCADAS] = prefs.evitarEscadas
            store[Keys.PRIORIZAR_RAMPAS] = prefs.priorizarRampas
            store[Keys.ALERTAS_VISUAIS] = prefs.alertasVisuais
            store[Keys.ALTO_CONTRASTE] = prefs.altoContraste
            store[Keys.FONT_SCALE] = prefs.fontScale.coerceIn(0.90f, 1.25f)
            store[Keys.USUARIO_ID] = prefs.usuarioId
        }
    }

    suspend fun saveApiConfig(config: ConexaoApiConfig) {
        context.dataStore.edit { store ->
            store[Keys.USE_API_MODE] = config.useApiMode
            store[Keys.API_BASE_URL] = config.apiBaseUrl.trim()
            store[Keys.BEARER_TOKEN] = config.bearerToken.trim()
        }
    }
}
