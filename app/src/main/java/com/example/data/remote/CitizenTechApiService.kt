package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CitizenTechApiService {

    @GET("locais")
    suspend fun getLocais(): List<LocalDto>

    @GET("locais/{id}")
    suspend fun getLocal(@Path("id") id: String): LocalDto

    @GET("ocorrencias")
    suspend fun getOcorrencias(@Query("usuarioId") usuarioId: String): List<OcorrenciaDto>

    @POST("ocorrencias")
    suspend fun criarOcorrencia(@Body request: CriarOcorrenciaRequest): OcorrenciaDto

    @PUT("ocorrencias/{id}/status")
    suspend fun atualizarStatus(
        @Path("id") id: String,
        @Body request: AtualizarStatusRequest,
    ): OcorrenciaDto

    @DELETE("ocorrencias/{id}")
    suspend fun excluirOcorrencia(@Path("id") id: String): Response<Unit>

    @GET("alertas")
    suspend fun getAlertas(): List<AlertaDto>

    @GET("preferencias/{usuarioId}")
    suspend fun getPreferencias(@Path("usuarioId") usuarioId: String): PreferenciasDto

    @PUT("preferencias/{usuarioId}")
    suspend fun salvarPreferencias(
        @Path("usuarioId") usuarioId: String,
        @Body request: PreferenciasDto,
    ): PreferenciasDto

    @POST("sensores/leituras")
    suspend fun registrarLeituras(@Body leituras: List<LeituraSensorRequest>): LeiturasResponseDto
}
