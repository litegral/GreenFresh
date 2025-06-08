package com.litegral.greenfresh.data

data class Plant(
    val id: Int,
    val plant_name: String,
    val description: String,
    val price: String,
    val created_at: String,
    val updated_at: String
)

data class PlantResponse(
    val message: String,
    val data: List<Plant>
) 