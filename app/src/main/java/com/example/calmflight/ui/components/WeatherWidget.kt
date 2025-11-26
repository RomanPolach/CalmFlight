package com.example.calmflight.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.calmflight.R
import com.example.calmflight.model.WeatherUiState
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.OrangeSafe
import com.example.calmflight.ui.theme.TealSoft
import com.google.android.gms.location.LocationServices

@Composable
fun WeatherWidget(
    weatherState: WeatherUiState?,
    onFetchWeather: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && weatherState == null) {
            getCurrentLocation(context) { lat, lon ->
                onFetchWeather(lat, lon)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyLight),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.weather_widget_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = BeigeWarm,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (weatherState?.cityName != null) {
                        Text(
                            text = weatherState.cityName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TealSoft,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Icon(
                    imageVector = if (weatherState?.isGoodForTakeoff == true) Icons.Default.WbSunny else Icons.Default.Cloud,
                    contentDescription = null,
                    tint = if (weatherState?.isGoodForTakeoff == true) OrangeSafe else TealSoft
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            if (!hasLocationPermission) {
                Button(
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealSoft),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.weather_check_btn))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.weather_permission_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = BeigeWarm.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (weatherState == null || weatherState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealSoft, modifier = Modifier.size(24.dp))
                }
            } else if (weatherState.errorRes != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = BeigeWarm.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(weatherState.errorRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BeigeWarm,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.weather_offline_reassurance),
                        style = MaterialTheme.typography.bodySmall,
                        color = BeigeWarm.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // Weather Data Display
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${weatherState.temperature}°C",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TealSoft,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.weather_wind_fmt, weatherState.windSpeed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = BeigeWarm
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (weatherState.isGoodForTakeoff) 
                                TealSoft.copy(alpha = 0.2f) 
                            else 
                                OrangeSafe.copy(alpha = 0.2f)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(weatherState.messageRes, *weatherState.messageArgs.toTypedArray()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BeigeWarm
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, onLocation: (Double, Double) -> Unit) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocation(location.latitude, location.longitude)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
