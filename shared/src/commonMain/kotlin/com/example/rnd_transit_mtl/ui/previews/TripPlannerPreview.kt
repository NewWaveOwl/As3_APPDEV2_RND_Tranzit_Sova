package com.example.rnd_transit_mtl.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.ui.TripPlannerContent
import com.example.rnd_transit_mtl.ui.theme.RNDTransitTheme

private val plannerPreviewTypes = listOf(
    TransportType("train", "Train", true),
    TransportType("rem", "REM", true),
    TransportType("walk", "Walk", false),
    TransportType("bus", "Bus", true),
    TransportType("metro", "Metro", true),
    TransportType("bike", "Bike", false)
)

private val plannerPreviewRoutes = listOf(
    TransportRoute("train:11", "train", "11"),
    TransportRoute("train:14", "train", "14"),
    TransportRoute("bus:401", "bus", "401"),
    TransportRoute("bus:747", "bus", "747")
)

@Preview(
    name = "Planner phone",
    showBackground = true,
    widthDp = 350,
    heightDp = 780
)
@Composable
fun TripPlannerPreview() {
    PlannerPreviewContent()
}

@Preview(
    name = "Planner desktop",
    showBackground = true,
    widthDp = 1000,
    heightDp = 900
)
@Composable
fun TripPlannerDesktopPreview() {
    PlannerPreviewContent()
}

@Preview(
    name = "Resume existing trip",
    showBackground = true,
    widthDp = 350,
    heightDp = 780
)
@Composable
fun TripPlannerResumePreview() {
    PlannerPreviewContent(resume = true)
}

@Preview(
    name = "Planner validation",
    showBackground = true,
    widthDp = 350,
    heightDp = 780
)
@Composable
fun TripPlannerValidationPreview() {
    PlannerPreviewContent(showErrors = true)
}

@Composable
private fun PlannerPreviewContent(
    resume: Boolean = false,
    showErrors: Boolean = false
) {
    RNDTransitTheme {
        TripPlannerContent(
            tripTitle = if (showErrors) "" else "Sunday discovery walk",
            onTripTitleChange = {},
            tripDescription = if (showErrors) {
                ""
            } else {
                "Discover interesting places with a walk and a bus ride."
            },
            onTripDescriptionChange = {},
            imageUrl = if (showErrors) {
                "invalid link"
            } else {
                "https://example.com/trip-reference.jpg"
            },
            onImageUrlChange = {},
            minutes = 30,
            onMinutesChange = {},
            transportTypes = plannerPreviewTypes,
            transportRoutes = plannerPreviewRoutes,
            selectedTransportIds = listOf("walk", "bus"),
            selectedRouteIds = listOf("bus:401"),
            expandedTransportId = null,
            onExpandedTransportChange = {},
            onToggleTransport = {},
            onToggleRoute = { _, _ -> },
            intensity = 90f,
            onIntensityChange = {},
            titleError = if (showErrors) "Enter a trip title." else null,
            descriptionError = if (showErrors) {
                "Enter a trip description."
            } else {
                null
            },
            imageUrlError = if (showErrors) {
                "Enter an HTTPS image URL with a valid host."
            } else {
                null
            },
            validationMessage = if (showErrors) {
                "Enter a trip title."
            } else {
                ""
            },
            actionLabel = if (resume) "Resume trip" else "GO",
            actionEnabled = true,
            inputsEnabled = true,
            activeTripSummary = if (resume) {
                "Unfinished: Sunday discovery walk · 50%"
            } else {
                null
            },
            pendingReviewSummary = null,
            onPrimaryAction = {}
        )
    }
}
