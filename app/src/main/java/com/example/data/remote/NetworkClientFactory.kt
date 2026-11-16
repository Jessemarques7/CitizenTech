package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class SessaoExpiradaException : IllegalStateException("Sua sessão expirou. Faça login novamente.")

object NetworkClientFactory {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun createApiService(baseUrl: String, bearerToken: String): CitizenTechApiService {
        val normalizedBaseUrl = normalizeBaseUrl(baseUrl)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                if (bearerToken.isNotBlank()) {
                    builder.addHeader("Authorization", "Bearer ${bearerToken.trim()}")
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizedBaseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CitizenTechApiService::class.java)
    }

    fun normalizeBaseUrl(rawUrl: String): String {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) return "http://10.0.2.2:8080/api/"
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    /** Converte falhas HTTP em exceções do app; 401 vira [SessaoExpiradaException] para forçar novo login. */
    fun toAppError(error: Throwable): Throwable =
        if (error is HttpException && error.code() == 401) {
            SessaoExpiradaException()
        } else {
            IllegalStateException(parseFriendlyError(error), error)
        }

    fun parseFriendlyError(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                val rawBody = error.response()?.errorBody()?.string()
                val parsedMessage = rawBody?.let {
                    runCatching {
                        moshi.adapter(ApiErrorDto::class.java).fromJson(it)
                    }.getOrNull()?.let { dto -> dto.message ?: dto.detail }
                }
                parsedMessage ?: "A API respondeu com status ${error.code()}."
            }
            is IOException -> "Não foi possível conectar à API. Verifique se o backend está ativo e acessível."
            is IllegalArgumentException -> "URL da API inválida. Informe uma URL completa (ex.: http://10.0.2.2:8080/api/)."
            else -> error.message ?: "Ocorreu um erro inesperado ao comunicar com a API."
        }
    }
}
