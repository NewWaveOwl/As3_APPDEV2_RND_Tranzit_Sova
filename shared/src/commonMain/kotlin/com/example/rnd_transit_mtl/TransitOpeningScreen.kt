package com.example.rnd_transit_mtl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.example.rnd_transit_mtl.data.MockTripGenerator
import com.example.rnd_transit_mtl.data.TripGenerationInput
import com.example.rnd_transit_mtl.data.TripGenerationResult
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.state.LocalTripsStore
import com.example.rnd_transit_mtl.state.TripActionResult
import com.example.rnd_transit_mtl.ui.TripPlannerContent

/**
 * Owns the saveable planner draft and coordinates GO/Resume.
 *
 * onOpenCurrentTrip receives the generated or existing stored Trip.
 * A true result means navigation was synchronously established.
 *
 * onOpenPendingReview follows the same navigation contract.
 * The pending event is acknowledged only after a true result.
 *
 * TripsStore remains the only owner of active/completed Trip records.
 */
@Composable
internal fun TransitOpeningScreen(
    transportTypes: List<TransportType>,
    transportRoutes: List<TransportRoute>,
    onOpenCurrentTrip: ((Trip) -> Boolean)? = null,
    onOpenPendingReview: ((String) -> Boolean)? = null,
    isDestinationActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val tripsStore = LocalTripsStore.current
    val focusManager = LocalFocusManager.current
    val generator = remember { MockTripGenerator() }

    var tripTitle by rememberSaveable { mutableStateOf("") }
    var tripDescription by rememberSaveable { mutableStateOf("") }
    var imageUrl by rememberSaveable { mutableStateOf("") }
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
    var validationAttempted by rememberSaveable {
        mutableStateOf(false)
    }
    var validationMessage by rememberSaveable {
        mutableStateOf("")
    }

    // This synchronous action lock is not a saved job or permanent form state.
    var processingAction by remember { mutableStateOf(false) }

    val latestDestinationActive by rememberUpdatedState(
        isDestinationActive
    )
    val latestOpenCurrentTrip by rememberUpdatedState(
        onOpenCurrentTrip
    )
    val latestOpenPendingReview by rememberUpdatedState(
        onOpenPendingReview
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
        validationAttempted = false
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

                focusManager.clearFocus()
                validationMessage = ""

                if (!openTrip(existingActive)) {
                    validationMessage =
                        "Your unfinished trip is preserved. Tap Resume trip to retry."
                }
                return
            }

            val pendingId = tripsStore.pendingReviewTripId
            if (pendingId != null) {
                val openReview = latestOpenPendingReview
                if (openReview == null) {
                    validationMessage = "Review navigation is unavailable."
                    return
                }

                focusManager.clearFocus()

                if (openReview(pendingId)) {
                    if (tripsStore.pendingReviewTripId == pendingId) {
                        tripsStore.acknowledgeReviewNavigation(pendingId)
                    }
                    validationMessage = ""
                } else {
                    validationMessage =
                        "The review has not opened. Your completed trip is preserved."
                }
                return
            }

            val openTrip = latestOpenCurrentTrip
            if (openTrip == null) {
                validationMessage = "Current Trip navigation is unavailable."
                return
            }

            validationAttempted = true
            validationMessage = ""

            val input = TripGenerationInput(
                title = tripTitle,
                description = tripDescription,
                imageUrl = imageUrl,
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
                    existingTripIds = existingIds
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
                            focusManager.clearFocus()

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

    val titleError = if (
        validationAttempted && tripTitle.isBlank()
    ) {
        "Enter a trip title."
    } else {
        null
    }

    val descriptionError = if (
        validationAttempted && tripDescription.isBlank()
    ) {
        "Enter a trip description."
    } else {
        null
    }

    val imageUrlError = if (
        validationAttempted &&
        !Trip.isSupportedImageUrl(imageUrl.trim())
    ) {
        "Enter an HTTPS image URL with a valid host."
    } else {
        null
    }

    val actionLabel = when {
        activeState != null -> "Resume trip"
        pendingReviewId != null -> "Continue review"
        else -> "GO"
    }

    val actionHasCallback = if (pendingReviewId != null) {
        onOpenPendingReview != null
    } else {
        onOpenCurrentTrip != null
    }

    TripPlannerContent(
        tripTitle = tripTitle,
        onTripTitleChange = {
            if (canChangeInputs()) {
                tripTitle = it
                clearValidation()
            }
        },
        tripDescription = tripDescription,
        onTripDescriptionChange = {
            if (canChangeInputs()) {
                tripDescription = it
                clearValidation()
            }
        },
        imageUrl = imageUrl,
        onImageUrlChange = {
            if (canChangeInputs()) {
                imageUrl = it
                clearValidation()
            }
        },
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
        titleError = titleError,
        descriptionError = descriptionError,
        imageUrlError = imageUrlError,
        validationMessage = validationMessage,
        actionLabel = actionLabel,
        actionEnabled = isDestinationActive &&
                !processingAction &&
                actionHasCallback,
        inputsEnabled = isDestinationActive && !processingAction,
        activeTripSummary = activeState?.let {
            val percentage = (
                    it.elapsedMillis.toFloat() / 10_000f * 100f
                    ).toInt().coerceIn(0, 99)

            "Unfinished: ${it.trip.title} · $percentage%"
        },
        pendingReviewSummary = pendingReviewTrip?.let {
            "Completed: ${it.title} · review ready"
        },
        onPrimaryAction = { performPrimaryAction() },
        modifier = modifier
    )
}

private fun List<String>.toggledPlannerId(id: String): List<String> =
    if (id in this) {
        filterNot { it == id }
    } else {
        this + id
    }
