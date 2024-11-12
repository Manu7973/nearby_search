package com.msa.msa_software.interfaces

import com.msa.msa_software.responseData.NearbyPlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyApi {
    @GET("v2/places")
    fun getNearbyPlaces(
        @Query("categories") categories: String,
        @Query("filter") filter: String,
        @Query("limit") limit: Int,
        @Query("apiKey") apiKey: String
    ): Call<NearbyPlacesResponse>
}
