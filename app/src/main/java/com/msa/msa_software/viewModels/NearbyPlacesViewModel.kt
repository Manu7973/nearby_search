package com.msa.msa_software.viewModels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.msa.msa_software.NetworkRequests.GeoRetrofitInstance
import com.msa.msa_software.responseData.NearbyPlacesResponse
import com.msa.msa_software.responseData.PlaceFeature
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyPlacesViewModel : ViewModel() {
    private val _placesList = MutableLiveData<List<PlaceFeature>>()
    val placesList: LiveData<List<PlaceFeature>> get() = _placesList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    fun fetchNearbyPlaces(categories: String, filter: String, apiKey: String) {
        _loading.value = true // Start loading
        GeoRetrofitInstance.api.getNearbyPlaces(categories, filter, 20, apiKey)
            .enqueue(object : Callback<NearbyPlacesResponse> {
                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (response.isSuccessful) {
                        _placesList.value = response.body()?.features ?: emptyList()
                    }
                    _loading.value = false // Stop loading
                }

                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    _loading.value = false // Stop loading even on failure
                    // Handle failure
                }
            })
    }
}

