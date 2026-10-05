package com.example.rnd_transit_mtl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rnd_transit_mtl.data.MockTripGenerator
import com.example.rnd_transit_mtl.data.TripGenerationBounds
import com.example.rnd_transit_mtl.data.TripGenerationInput
import com.example.rnd_transit_mtl.data.TripGenerationResult
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.state.LocalTripsStore
import com.example.rnd_transit_mtl.state.TripActionResult
import com.example.rnd_transit_mtl.ui.TripPlannerContent

/** Saves planner selections and starts or resumes one generated mock trip. */
@Composable
internal fun TransitOpeningScreen(
    transportTypes: List<TransportType>,
    transportRoutes: List<TransportRoute>,
    onOpenCurrentTrip: ((Trip) -> Boolean)? = null,
    onOpenCompletedTrip: ((String) -> Boolean)? = null,
    isDestinationActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val tripsStore = LocalTripsStore.current
    val generator = remember { MockTripGenerator() }
    // Viewport is measured again after rotation; stored trip endpoints are unchanged.
    var generationBounds by remember { mutableStateOf<TripGenerationBounds?>(null) }

    var minutes by rememberSaveable { mutableStateOf(30) }

    var selectedTransportIds by rememberSaveable {
        mutableStateOf(
            if (transportTypes.any { it.id == "walk" }) {
                listOf("walk")
            } else {
                emptyList<String>()
            }
        )
    }

    var selectedRouteIds by rememberSaveable {
        mutableStateOf(emptyList<String>())
    }
    var expandedTransportId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var intensity by rememberSaveable { mutableStateOf(90f) }
    var validationMessage by rememberSaveable {
        mutableStateOf("")
    }

    // Blocks overlapping GO actions; the store also prevents another active trip.
    var processingAction by remember { mutableStateOf(false) }

    val latestDestinationActive by rememberUpdatedState(
        isDestinationActive
    )
    val latestOpenCurrentTrip by rememberUpdatedState(
        onOpenCurrentTrip
    )
    val latestOpenCompletedTrip by rememberUpdatedState(
        onOpenCompletedTrip
    )

    val currentState by tripsStore.state
    val activeState = currentState.activeTrip
    val pendingReviewId = currentState.pendingReviewTripId
    val pendingReviewTrip = pendingReviewId?.let { id ->
        currentState.completedTrips.firstOrNull { it.id == id }
    }

    fun canChangeInputs(): Boolean =
        latestDestinationActive && !processingAction

    fun clearValidation() {
        validationMessage = ""
    }

    fun performPrimaryAction() {
        if (!latestDestinationActive || processingAction) return

        processingAction = true

        try {
            // Re-read the store at click time, including before generation.
            val existingActive = tripsStore.activeTrip
            if (existingActive != null) {
                val openTrip = latestOpenCurrentTrip
                if (openTrip == null) {
                    validationMessage = "Current Trip navigation is unavailable."
                    return
                }
                validationMessage = ""

                if (!openTrip(existingActive)) {
                    validationMessage =
                        "Your unfinished trip is preserved. Tap Resume trip to retry."
                }
                return
            }

            val pendingId = tripsStore.pendingReviewTripId
            if (pendingId != null) {
                val openCompleted = latestOpenCompletedTrip
                if (openCompleted == null || !openCompleted(pendingId)) {
                    validationMessage = "Your completed trip is saved. Try opening it again."
                } else {
                    validationMessage = ""
                }
                return
            }

            val openTrip = latestOpenCurrentTrip
            if (openTrip == null) {
                validationMessage = "Current Trip navigation is unavailable."
                return
            }

            val bounds = generationBounds
            if (bounds == null) {
                validationMessage = "The map is preparing. Please try GO again."
                return
            }
            validationMessage = ""

            val input = TripGenerationInput(
                title = "Trip ${tripsStore.completedTrips.size + 1}",
                description = "Random mock route with $minutes planned minutes.",
                imageUrl = "",
                plannedMinutes = minutes,
                selectedTransportIds = selectedTransportIds.toList(),
                selectedRouteIds = selectedRouteIds.toList(),
                attractionIntensity = intensity
            )

            val existingIds = buildSet {
                addAll(tripsStore.completedTrips.map { it.id })
                tripsStore.activeTrip?.let { add(it.id) }
            }

            when (
                val generated = generator.generate(
                    input = input,
                    transportTypes = transportTypes,
                    transportRoutes = transportRoutes,
                    existingTripIds = existingIds,
                    bounds = bounds
                )
            ) {
                is TripGenerationResult.InvalidInput -> {
                    validationMessage = generated.message
                }

                TripGenerationResult.IdUnavailable -> {
                    validationMessage =
                        "A unique trip ID could not be created. Please try again."
                }

                is TripGenerationResult.Success -> {
                    when (val started = tripsStore.start(generated.trip)) {
                        TripActionResult.Applied -> {

                            if (!openTrip(generated.trip)) {
                                validationMessage =
                                    "Your trip started and is preserved. " +
                                            "Tap Resume trip to open it."
                            }
                        }

                        is TripActionResult.ActiveTripExists -> {
                            validationMessage =
                                "An unfinished trip already exists. Use Resume trip."
                        }

                        is TripActionResult.InvalidInput -> {
                            validationMessage = started.message
                        }

                        else -> {
                            validationMessage =
                                "The trip could not be started. Please try again."
                        }
                    }
                }
            }
        } finally {
            processingAction = false
        }
    }

    val actionLabel = when {
        activeState != null -> "Resume trip"
        pendingReviewId != null -> "100%"
        else -> "GO"
    }

    val actionHasCallback = if (activeState == null && pendingReviewId != null) {
        onOpenCompletedTrip != null
    } else {
        onOpenCurrentTrip != null
    }

    TripPlannerContent(
        minutes = minutes,
        onMinutesChange = {
            if (canChangeInputs()) {
                minutes = it
                    .coerceIn(
                        Trip.MIN_PLANNED_MINUTES,
                        Trip.MAX_PLANNED_MINUTES
                    )
                    .let { bounded ->
                        bounded -
                                bounded % Trip.PLANNED_MINUTES_STEP
                    }
                clearValidation()
            }
        },
        transportTypes = transportTypes,
        transportRoutes = transportRoutes,
        selectedTransportIds = selectedTransportIds,
        selectedRouteIds = selectedRouteIds,
        expandedTransportId = expandedTransportId,
        onExpandedTransportChange = { transportId ->
            if (
                canChangeInputs() &&
                transportTypes.any {
                    it.id == transportId && it.usesRoutes
                }
            ) {
                expandedTransportId =
                    if (expandedTransportId == transportId) {
                        null
                    } else {
                        transportId
                    }
            }
        },
        onToggleTransport = { transportId ->
            val transport = transportTypes.firstOrNull {
                it.id == transportId
            }

            if (
                canChangeInputs() &&
                transport != null &&
                !transport.usesRoutes
            ) {
                selectedTransportIds =
                    selectedTransportIds.toggledPlannerId(transportId)
                clearValidation()
            }
        },
        onToggleRoute = { transportId, routeId ->
            val transport = transportTypes.firstOrNull {
                it.id == transportId && it.usesRoutes
            }
            val route = transportRoutes.firstOrNull {
                it.id == routeId &&
                        it.transportTypeId == transportId
            }

            if (
                canChangeInputs() &&
                transport != null &&
                route != null
            ) {
                selectedRouteIds =
                    selectedRouteIds.toggledPlannerId(routeId)

                val hasSelectedRoute = transportRoutes.any {
                    it.transportTypeId == transportId &&
                            it.id in selectedRouteIds
                }

                selectedTransportIds = if (hasSelectedRoute) {
                    if (transportId in selectedTransportIds) {
                        selectedTransportIds
                    } else {
                        selectedTransportIds + transportId
                    }
                } else {
                    selectedTransportIds.filterNot {
                        it == transportId
                    }
                }

                clearValidation()
            }
        },
        intensity = intensity,
        onIntensityChange = {
            if (canChangeInputs() && it.isFinite()) {
                intensity = it.coerceIn(
                    Trip.MIN_ATTRACTION_INTENSITY,
                    Trip.MAX_ATTRACTION_INTENSITY
                )
                clearValidation()
            }
        },
        validationMessage = validationMessage,
        actionLabel = actionLabel,
        actionEnabled = isDestinationActive &&
                !processingAction &&
                actionHasCallback &&
                (activeState != null || pendingReviewId != null || generationBounds != null),
        activeTripSummary = activeState?.let {
            val percentage = (
                    it.elapsedMillis.toFloat() / 10_000f * 100f
                    ).toInt().coerceIn(0, 99)

            "Unfinished: ${it.trip.title} · $percentage%"
        },
        pendingReviewSummary = pendingReviewTrip?.let {
            "Completed: ${it.title} · tap 100% to open your trip"
        },
        onPrimaryAction = { performPrimaryAction() },
        controlsVisible = isDestinationActive,
        onMapViewportReady = { generationBounds = it },
        modifier = modifier
    )
}

private fun List<String>.toggledPlannerId(id: String): List<String> =
    if (id in this) {
        filterNot { it == id }
    } else {
        this + id
    }
