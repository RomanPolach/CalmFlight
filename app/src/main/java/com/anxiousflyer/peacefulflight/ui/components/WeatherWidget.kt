package com.anxiousflyer.peacefulflight.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.model.WeatherUiState
import com.anxiousflyer.peacefulflight.utils.UnitConverter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun WeatherWidget(
    weatherState: WeatherUiState?,
    onFetchWeather: (Double, Double) -> Unit,
    onLocationError: (Int) -> Unit,
    onRetry: () -> Unit,
    isMetric: Boolean
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

    LaunchedEffect(hasLocationPermission, weatherState) {
        if (hasLocationPermission && weatherState == null) {
            getCurrentLocation(
                context = context,
                onLocation = { lat, lon -> onFetchWeather(lat, lon) },
                onError = { onLocationError(R.string.weather_error_location) }
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (weatherState?.cityName != null) {
                        Text(
                            text = weatherState.cityName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                if (weatherState != null && !weatherState.isLoading && weatherState.errorRes == null) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = weatherState.getWeatherIcon(),
                        contentDescription = null,
                        tint = weatherState.getWeatherIconTint()
                    )
                }
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (weatherState == null || weatherState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (weatherState.errorRes != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(weatherState.errorRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(stringResource(R.string.weather_retry_btn))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.weather_offline_reassurance),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // Weather Data Display
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = UnitConverter.formatTemperature(weatherState.temperature, isMetric),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        // Weather description on same line as wind
                        if (weatherState.weatherDescription.isNotEmpty()) {
                            Text(
                                text = weatherState.weatherDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Wind: ${
                                UnitConverter.formatWindSpeed(
                                    weatherState.windSpeed,
                                    isMetric
                                )
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Passenger Message (reassuring interpretation)
                if (weatherState.passengerMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (weatherState.isGoodForTakeoff)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = weatherState.passengerMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
    onError: () -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Priority 1: Use last known location for speed
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocation(location.latitude, location.longitude)
            } else {
                // Priority 2: Request fresh location if lastLocation is null
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { freshLocation ->
                    if (freshLocation != null) {
                        onLocation(freshLocation.latitude, freshLocation.longitude)
                    } else {
                        onError()
                    }
                }.addOnFailureListener {
                    onError()
                }
            }
        }.addOnFailureListener {
            onError()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onError()
    }
}

@Composable
private fun WeatherUiState?.getWeatherIcon(): ImageVector {
    return when (this?.weatherCode) {
        0, 1 -> Icons.Default.WbSunny
        2 -> ImageVector.vectorResource(R.drawable.ic_partly_cloudy)
        3, 45, 48, 95, 96, 99 -> Icons.Default.Cloud
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> ImageVector.vectorResource(R.drawable.ic_rain)
        71, 73, 75, 77, 85, 86 -> ImageVector.vectorResource(R.drawable.ic_snow)
        else -> Icons.Default.Cloud
    }
}

@Composable
private fun WeatherUiState?.getWeatherIconTint() = when (this?.weatherCode) {
    0, 1 -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.primary
}
