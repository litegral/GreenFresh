package com.litegral.greenfresh.data.remote

import com.litegral.greenfresh.data.CreatePlantResponse
import com.litegral.greenfresh.data.PlantRequestBody
import com.litegral.greenfresh.data.PlantResponse
import com.litegral.greenfresh.data.PlantDetailResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("plant/all")
    suspend fun getPlants(): PlantResponse

    @GET("plant/{name}")
    suspend fun getPlantByName(@Path("name") name: String): PlantDetailResponse

    @POST("plant/new")
    suspend fun createPlant(@Body plant: PlantRequestBody): CreatePlantResponse

    @PUT("plant/{name}")
    suspend fun updatePlant(@Path("name") name: String, @Body plant: PlantRequestBody): CreatePlantResponse

    @DELETE("plant/{name}")
    suspend fun deletePlant(@Path("name") name: String): CreatePlantResponse
} 