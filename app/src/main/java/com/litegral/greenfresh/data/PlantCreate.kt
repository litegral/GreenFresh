package com.litegral.greenfresh.data

import com.google.gson.annotations.SerializedName

data class PlantRequestBody(
    @SerializedName("plant_name")
    val plantName: String,
    val description: String,
    val price: String
)

data class CreatePlantResponse(
    val message: String,
    val data: Plant
) 