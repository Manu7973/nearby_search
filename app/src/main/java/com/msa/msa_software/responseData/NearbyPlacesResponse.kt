package com.msa.msa_software.responseData

import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(
    @SerializedName("type")
    val type: String,

    @SerializedName("features")
    val features: List<PlaceFeature>
)

data class PlaceFeature(
    @SerializedName("properties")
    val properties: PlaceProperties,

    @SerializedName("geometry")
    val geometry: Geometry
)

data class PlaceProperties(
    @SerializedName("name")
    val name: String,

    @SerializedName("formatted")
    val formattedAddress: String
)

data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<Double>
)

