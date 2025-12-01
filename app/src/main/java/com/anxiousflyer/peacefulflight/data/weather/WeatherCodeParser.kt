package com.anxiousflyer.peacefulflight.data.weather

/**
 * WMO Weather Code Parser
 * Provides precise weather descriptions and flight condition interpretations
 * Designed for nervous flyer app - balances accuracy with reassurance
 */
object WeatherCodeParser {

    /**
     * Flight condition categories for aviation context
     */
    enum class FlightCondition {
        EXCELLENT,  // Perfect flying conditions
        GOOD,       // Safe, minor considerations
        CAUTION,    // Proceed with awareness
        POOR        // Challenging conditions
    }

    data class WeatherInfo(
        val description: String,
        val flightCondition: FlightCondition,
        val passengerMessage: String,      // Reassuring message for nervous flyers
        val technicalInfo: String          // Accurate info for pilots/technical users
    )

    /**
     * Get complete weather information for a WMO code
     */
    fun getWeatherInfo(code: Int): WeatherInfo {
        return when (code) {
            // Clear conditions
            0 -> WeatherInfo(
                description = "Clear sky",
                flightCondition = FlightCondition.EXCELLENT,
                passengerMessage = "Beautiful clear skies - perfect conditions for a smooth flight",
                technicalInfo = "VFR conditions, unlimited visibility"
            )

            // Partly cloudy conditions
            1 -> WeatherInfo(
                description = "Mainly clear",
                flightCondition = FlightCondition.EXCELLENT,
                passengerMessage = "Mostly clear with excellent visibility",
                technicalInfo = "Excellent VFR conditions"
            )

            2 -> WeatherInfo(
                description = "Partly cloudy",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Some clouds present - normal flying weather",
                technicalInfo = "Good VFR conditions"
            )

            3 -> WeatherInfo(
                description = "Overcast",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Cloudy skies - aircraft handle this routinely",
                technicalInfo = "Overcast, possible IFR conditions"
            )

            // Fog conditions
            45 -> WeatherInfo(
                description = "Fog",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Foggy conditions - pilots use instruments for safe navigation",
                technicalInfo = "Reduced visibility, IFR procedures"
            )

            48 -> WeatherInfo(
                description = "Depositing rime fog",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Fog present - aircraft equipped with de-icing systems",
                technicalInfo = "Limited visibility, icing possible"
            )

            // Drizzle conditions
            51 -> WeatherInfo(
                description = "Light drizzle",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Light drizzle - planes fly safely in light rain",
                technicalInfo = "Minor precipitation, good visibility"
            )

            53 -> WeatherInfo(
                description = "Moderate drizzle",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Wet conditions - aircraft designed for all-weather operation",
                technicalInfo = "Moderate precipitation, adequate visibility"
            )

            55 -> WeatherInfo(
                description = "Dense drizzle",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Heavy drizzle - pilots trained for these conditions",
                technicalInfo = "Heavy drizzle, reduced visibility"
            )

            // Freezing drizzle
            56 -> WeatherInfo(
                description = "Light freezing drizzle",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Cold weather precipitation - de-icing systems active",
                technicalInfo = "Light icing conditions, de-icing required"
            )

            57 -> WeatherInfo(
                description = "Dense freezing drizzle",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Winter weather - aircraft have advanced ice protection",
                technicalInfo = "Significant icing risk, enhanced monitoring"
            )

            // Rain conditions
            61 -> WeatherInfo(
                description = "Slight rain",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Light rain - planes are built to fly in wet weather",
                technicalInfo = "Light rain, good conditions"
            )

            63 -> WeatherInfo(
                description = "Moderate rain",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Rainy weather - standard conditions for commercial aviation",
                technicalInfo = "Moderate rain, reduced visibility"
            )

            65 -> WeatherInfo(
                description = "Heavy rain",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Heavy rain - pilots use instruments for precise navigation",
                technicalInfo = "Heavy precipitation, limited visibility"
            )

            // Freezing rain
            66 -> WeatherInfo(
                description = "Light freezing rain",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Cold rain - aircraft equipped with comprehensive de-icing",
                technicalInfo = "Icing conditions, careful monitoring required"
            )

            67 -> WeatherInfo(
                description = "Heavy freezing rain",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Winter precipitation - flights may be delayed for safety",
                technicalInfo = "Severe icing risk, challenging conditions"
            )

            // Snow conditions
            71 -> WeatherInfo(
                description = "Slight snow fall",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Light snow - airports have snow removal teams ready",
                technicalInfo = "Light snow, visibility reduced"
            )

            73 -> WeatherInfo(
                description = "Moderate snow fall",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Snowy conditions - de-icing procedures in effect",
                technicalInfo = "Moderate snow, limited visibility"
            )

            75 -> WeatherInfo(
                description = "Heavy snow fall",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Heavy snow - flights may wait for better conditions",
                technicalInfo = "Heavy snow, significantly reduced visibility"
            )

            // Snow grains
            77 -> WeatherInfo(
                description = "Snow grains",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Winter precipitation - normal for cold weather flying",
                technicalInfo = "Snow grains, reduced visibility"
            )

            // Rain showers
            80 -> WeatherInfo(
                description = "Slight rain showers",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Passing showers - brief and manageable",
                technicalInfo = "Light showers, generally favorable"
            )

            81 -> WeatherInfo(
                description = "Moderate rain showers",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Rain showers - may cause brief bumps, perfectly safe",
                technicalInfo = "Moderate showers, possible turbulence"
            )

            82 -> WeatherInfo(
                description = "Violent rain showers",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Heavy showers - pilots can navigate around weather cells",
                technicalInfo = "Intense showers, turbulence likely"
            )

            // Snow showers
            85 -> WeatherInfo(
                description = "Slight snow showers",
                flightCondition = FlightCondition.CAUTION,
                passengerMessage = "Snow showers - aircraft equipped for winter operations",
                technicalInfo = "Light snow showers, variable visibility"
            )

            86 -> WeatherInfo(
                description = "Heavy snow showers",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Heavy snow showers - flights may be rescheduled for safety",
                technicalInfo = "Heavy snow, significantly reduced visibility"
            )

            // Thunderstorms
            95 -> WeatherInfo(
                description = "Thunderstorm",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Thunderstorm activity - pilots route around storm cells",
                technicalInfo = "Thunderstorm, turbulence and lightning present"
            )

            96 -> WeatherInfo(
                description = "Thunderstorm with slight hail",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Storm with hail - flights typically delayed until it passes",
                technicalInfo = "Thunderstorm with hail, avoid area"
            )

            99 -> WeatherInfo(
                description = "Thunderstorm with heavy hail",
                flightCondition = FlightCondition.POOR,
                passengerMessage = "Severe storm - flights wait for weather to clear",
                technicalInfo = "Severe thunderstorm with heavy hail"
            )

            // Unknown code
            else -> WeatherInfo(
                description = "Weather information unavailable",
                flightCondition = FlightCondition.GOOD,
                passengerMessage = "Weather data is being updated",
                technicalInfo = "Weather code unknown"
            )
        }
    }

    /**
     * Get just the weather description
     */
    fun getDescription(code: Int): String = getWeatherInfo(code).description

    /**
     * Get just the flight condition
     */
    fun getFlightCondition(code: Int): FlightCondition = getWeatherInfo(code).flightCondition

    /**
     * Get passenger-friendly message (reassuring)
     */
    fun getPassengerMessage(code: Int): String = getWeatherInfo(code).passengerMessage

    /**
     * Get technical flight information (accurate for pilots)
     */
    fun getTechnicalInfo(code: Int): String = getWeatherInfo(code).technicalInfo

    /**
     * Check if weather is suitable for flying (Excellent or Good conditions)
     */
    fun isSuitableForFlying(code: Int): Boolean {
        val condition = getFlightCondition(code)
        return condition == FlightCondition.EXCELLENT || condition == FlightCondition.GOOD
    }
}
