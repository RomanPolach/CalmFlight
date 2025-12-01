package com.anxiousflyer.peacefulflight.data

import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.model.Category
import com.anxiousflyer.peacefulflight.model.LearnItem
import com.anxiousflyer.peacefulflight.model.LearnSection
import com.anxiousflyer.peacefulflight.model.Tool

object AppContent {

    val learnSections = listOf(
        LearnSection(
            id = "takeoff",
            titleRes = R.string.learn_section_takeoff,
            items = listOf(
                LearnItem("takeoff_1", R.string.takeoff_q1, R.string.takeoff_a1),
                LearnItem("takeoff_3", R.string.takeoff_q3, R.string.takeoff_a3),
                LearnItem("takeoff_5", R.string.takeoff_q5, R.string.takeoff_a5),
                LearnItem("takeoff_6", R.string.takeoff_q6, R.string.takeoff_a6),
                LearnItem("takeoff_7", R.string.takeoff_q7, R.string.takeoff_a7),
                LearnItem(
                    "takeoff_8",
                    R.string.takeoff_q8,
                    R.string.takeoff_a8,
                    R.drawable.bird_strike_test,
                    R.string.img_title_bird_strike_test
                )
            )
        ),
        LearnSection(
            id = "flight",
            titleRes = R.string.learn_section_flight,
            items = listOf(
                LearnItem("flight_1", R.string.flight_q1, R.string.flight_a1),
                LearnItem("flight_2", R.string.flight_q2, R.string.flight_a2),
                LearnItem("flight_3", R.string.flight_q3, R.string.flight_a3),
                LearnItem("flight_4", R.string.flight_q4, R.string.flight_a4),
                LearnItem("flight_5", R.string.flight_q5, R.string.flight_a5),
                LearnItem("flight_6", R.string.flight_q6, R.string.flight_a6),
                LearnItem("flight_7", R.string.flight_q7, R.string.flight_a7),
                LearnItem(
                    "flight_8",
                    R.string.flight_q8,
                    R.string.flight_a8,
                    R.drawable.deicing_a330,
                    R.string.img_title_deicing
                ),
                LearnItem(
                    "flight_10",
                    R.string.flight_q10,
                    R.string.flight_a10,
                    R.drawable.ram_air_turbine_a320,
                    R.string.img_title_rat
                ),
                LearnItem("flight_11", R.string.flight_q11, R.string.flight_a11)
            )
        ),
        LearnSection(
            id = "landing",
            titleRes = R.string.learn_section_landing,
            items = listOf(
                LearnItem("landing_1", R.string.landing_q1, R.string.landing_a1),
                LearnItem(
                    "landing_2",
                    R.string.landing_q2,
                    R.string.landing_a2,
                    R.drawable.wing_flex_787,
                    R.string.img_title_wing_flex
                ),
                LearnItem("landing_3", R.string.landing_q3, R.string.landing_a3),
                LearnItem(
                    "landing_4",
                    R.string.landing_q4,
                    R.string.landing_a4,
                    R.drawable.airport_runway_lights,
                    R.string.img_title_airport_lights
                ),
                LearnItem(
                    "landing_5",
                    R.string.landing_q5,
                    R.string.landing_a5,
                    R.drawable.crosswind_landing_a380,
                    R.string.img_title_crosswind_landing
                ),
                LearnItem(
                    "landing_9",
                    R.string.landing_q9,
                    R.string.landing_a9,
                    R.drawable.egpws_display,
                    R.string.img_title_egpws
                )
            )
        )
    )

    val categories = listOf(
        Category("A", "Turbulence & Physics", "The 'Bumps'"),
        Category("B", "Scary Sounds & Sensations", "What was that noise?"),
        Category("C", "Weather & Environment", "Lightning, fog, and clouds"),
        Category("D", "Mechanical & What Ifs", "Safety systems"),
        Category("E", "Crew & Protocol", "Pilots and Flight Attendants")
    )


    val tools = listOf(
        Tool("3", "G-Force Monitor", "Visual proof of stability", "Graph"),
        Tool("5", "Riding The Wave", "Stop a panic attack", "Wave"),
        Tool("6", "Postpone the Worry", "Stop 'what-if' thoughts", "Clock"),
        Tool("7", "Worry Olympics", "Exaggerate fears to reduce them", "Trophy"),
        Tool("8", "Facing the Fear", "Fly into the cloud", "Cloud"),
        Tool("9", "Reality Check", "Compare your fear vs reality", "Chart"),
        Tool("10", "Safety Facts", "See why flying is safe", "Shield"),
        Tool("11", "Acceptance Meditation", "Work with anxiety", "Meditation"),
        Tool("12", "Self-Compassion", "Meet fear with kindness", "Heart"),
        Tool(
            "13", "Catastrophic Thinking", "" +
                    "Investigate your worries", "Brain"
        )
    )
}
