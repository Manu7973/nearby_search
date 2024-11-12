package com.msa.msa_software.NetworkRequests

import com.msa.msa_software.interfaces.GeoapifyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoRetrofitInstance {
    private val BASE_URL = "https://api.geoapify.com/"

    val api: GeoapifyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoapifyApi::class.java)
    }
}