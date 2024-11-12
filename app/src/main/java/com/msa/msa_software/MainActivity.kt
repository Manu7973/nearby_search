package com.msa.msa_software


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.msa.msa_software.responseData.PlaceFeature
import com.msa.msa_software.viewModels.NearbyPlacesViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyLocationScreen()
        }
    }

    @Composable
    fun MyLocationScreen() {
        var location by remember { mutableStateOf<Location?>(null) }
        var permissionGranted by remember { mutableStateOf(false) }

        LocationPermissionScreen(
            onLocationFetched = { fetchedLocation ->
                location = fetchedLocation
            },
            onPermissionGranted = { granted ->
                permissionGranted = granted
            }
        )

        if (permissionGranted) {
            SearchFoodScreen()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD)), // Light blue background
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please provide location permission",
                    fontSize = 18.sp,
                    color = androidx.compose.ui.graphics.Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun LocationPermissionScreen(
        onLocationFetched: (Location) -> Unit,
        onPermissionGranted: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }

        // Launcher for request location permission
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            onPermissionGranted(isGranted)
            if (isGranted) {
                fetchLocation(fusedLocationClient, onLocationFetched)
            } else {
                println("Location permission denied.")
            }
        }

        // Check if location permission is already granted.
        LaunchedEffect(Unit) {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            onPermissionGranted(isPermissionGranted)
            if (isPermissionGranted) {
                fetchLocation(fusedLocationClient, onLocationFetched)
            } else {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @Composable
    fun SearchFoodScreen() {
        var selectedOption by remember { mutableStateOf("") }
        val options = listOf("Juice", "Pizza")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Search Food",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                FoodDropDown(selectedOption, onOptionSelected = { option ->
                    selectedOption = option
                }, options)

                Spacer(modifier = Modifier.height(20.dp))

                SearchButton(selectedOption)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationFetched: (Location) -> Unit
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationFetched(it)
            }
        }
    }

    //Search food dropDown
    @Composable
    fun FoodDropDown(
        selectedOption: String,
        onOptionSelected: (String) -> Unit,
        options: List<String>
    ) {
        var expanded by remember { mutableStateOf(false) }
        val density = LocalDensity.current.density

        Column {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("Search food") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            )

            // Adjust the positioning of the DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (density * 4).dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    ) {
                        Text(option)
                    }
                }
            }
        }
    }

    //Search button
    @Composable
    fun SearchButton(selectedOption: String) {
        val viewModel: NearbyPlacesViewModel = viewModel()

        // Observing the placesList and loading state
        val placesList by viewModel.placesList.observeAsState(emptyList())
        val isLoading by viewModel.loading.observeAsState(false)

        Button(
            onClick = {
                val apiKey = "b66c84259a164f1c8ef01eb391c5c44f"
                val filter = "rect:72.83030357894899,19.127714428394505,72.94089642105064,19.021855513590502"
                viewModel.fetchNearbyPlaces("catering.fast_food", filter, apiKey)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = selectedOption.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5),
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Text("Search", fontSize = 18.sp)
        }

        // Displaying content based on loading state
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                // Show loading indicator when data is loading
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // Show the list of places when data is available
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(placesList) { place ->
                        PlaceItem(place)
                    }
                }
            }
        }
    }

    @Composable
    fun PlaceItem(place: PlaceFeature) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = place.properties.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = place.properties.formattedAddress, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}