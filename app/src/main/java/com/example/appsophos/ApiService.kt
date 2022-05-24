package com.example.appsophos

import com.example.appsophos.dataclasses.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun GetUserByCredentials(@Url url:String): Response<AccessResponse>   // get de usuario

    @POST("/RS_Documentos")
    suspend fun PostDocuments(@Body requestBody: RequestBody): Response<PutDocumentsResponse>

    @GET
    suspend fun GetDocsInfo(@Url url:String): Response<DocumentsResponse> // get de documentos subidos

    @GET
    suspend fun GetOffsData(@Url url:String): Response<OfficeResponse> // get de oficinas

    @GET
    suspend fun GetImagesById(@Url url:String): Response<ImageResponse> // get de imagenes por id
}