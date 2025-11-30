package com.example.calmflight.data.weather

/**
 * Example usage of WeatherCodeParser
 *
 * This demonstrates how to use the weather code parser in your UI components
 * The parser provides TWO types of messages:
 * - passengerMessage: Reassuring, calming messages for nervous flyers
 * - technicalInfo: Accurate technical information for pilots/aviation professionals
 */

// Example 1: Get complete weather information
fun exampleGetWeatherInfo() {
    val code = 61 // Slight rain
    val info = WeatherCodeParser.getWeatherInfo(code)

    println("Description: ${info.description}")
    // Output: "Slight rain"

    println("Flight Condition: ${info.flightCondition}")
    // Output: "GOOD"

    println("Passenger Message: ${info.passengerMessage}")
    // Output: "Light rain - planes are built to fly in wet weather"

    println("Technical Info: ${info.technicalInfo}")
    // Output: "Light rain, good conditions"
}

// Example 2: Get individual components
fun exampleGetIndividualInfo() {
    val code = 95 // Thunderstorm

    val description = WeatherCodeParser.getDescription(code)
    // "Thunderstorm"

    val condition = WeatherCodeParser.getFlightCondition(code)
    // FlightCondition.POOR

    val passengerMsg = WeatherCodeParser.getPassengerMessage(code)
    // "Thunderstorm activity - pilots route around storm cells"
    // Note: Reassuring, explains what pilots do

    val technicalInfo = WeatherCodeParser.getTechnicalInfo(code)
    // "Thunderstorm, turbulence and lightning present"
    // Note: Accurate technical description
}

// Example 3: Check if suitable for flying
fun exampleCheckSuitability() {
    val clearSky = 0
    val thunderstorm = 95

    val canFlyInClear = WeatherCodeParser.isSuitableForFlying(clearSky)
    // true (EXCELLENT condition)

    val canFlyInStorm = WeatherCodeParser.isSuitableForFlying(thunderstorm)
    // false (POOR condition)
}

// Example 4: Display in UI (Jetpack Compose example)
/*
@Composable
fun WeatherDisplay(weatherState: WeatherUiState) {
    Column {
        // Show precise weather description
        Text(
            text = weatherState.weatherDescription,
            style = MaterialTheme.typography.titleMedium
        )
        
        // Show PASSENGER-FRIENDLY message (reassuring for nervous flyers)
        Text(
            text = weatherState.passengerMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = when (WeatherCodeParser.getFlightCondition(weatherState.weatherCode)) {
                WeatherCodeParser.FlightCondition.EXCELLENT -> Color(0xFF4CAF50) // Calming green
                WeatherCodeParser.FlightCondition.GOOD -> Color(0xFF8BC34A)      // Light green
                WeatherCodeParser.FlightCondition.CAUTION -> Color(0xFFFFA726)   // Soft orange
                WeatherCodeParser.FlightCondition.POOR -> Color(0xFFFF7043)      // Soft red (not alarming)
            }
        )
        
        // Optionally show technical info for pilots/aviation enthusiasts
        // (You might want to hide this for nervous flyers)
        if (userIsPilotOrEnthusiast) {
            Text(
                text = WeatherCodeParser.getTechnicalInfo(weatherState.weatherCode),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
*/

// Example 5: All weather codes and their interpretations
fun exampleAllCodes() {
    val codes = listOf(
        0,                    // Clear
        1, 2, 3,             // Partly cloudy
        45, 48,              // Fog
        51, 53, 55,          // Drizzle
        56, 57,              // Freezing drizzle
        61, 63, 65,          // Rain
        66, 67,              // Freezing rain
        71, 73, 75,          // Snow
        77,                  // Snow grains
        80, 81, 82,          // Rain showers
        85, 86,              // Snow showers
        95, 96, 99           // Thunderstorms
    )

    codes.forEach { code ->
        val info = WeatherCodeParser.getWeatherInfo(code)
        println("Code $code: ${info.description} - ${info.flightCondition}")
    }
}
