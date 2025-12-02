package com.anxiousflyer.peacefulflight.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anxiousflyer.peacefulflight.R

data class LearnSection(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val imageRes: Int? = null,
    val items: List<LearnItem>
)

data class LearnItem(
    val id: String,
    @StringRes val questionRes: Int,
    @StringRes val answerRes: Int,
    @DrawableRes val imageRes: Int? = null,
    @StringRes val imageTitleRes: Int? = null
)

data class Question(
    val id: String,
    val question: String,
    val answer: String,
    val categoryId: String
)

data class Category(
    val id: String,
    val title: String,
    val description: String
    // Icon handled in UI mapping or adding here if using material icons
)

data class Tool(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String // identifying string for icon mapping
)

enum class FlightStatus(@StringRes val labelRes: Int) {
    ON_LAND(R.string.status_on_land),
    BOARDING(R.string.status_boarding),
    TAKEOFF(R.string.status_takeoff),
    CRUISE(R.string.status_cruise),
    TURBULENCE(R.string.status_turbulence),
    LANDING(R.string.status_landing)
}
