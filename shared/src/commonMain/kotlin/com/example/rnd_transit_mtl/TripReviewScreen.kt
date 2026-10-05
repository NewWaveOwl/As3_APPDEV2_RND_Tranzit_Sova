package com.example.rnd_transit_mtl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.example.rnd_transit_mtl.model.TripReviewMode
import com.example.rnd_transit_mtl.state.LocalTripsStore
import com.example.rnd_transit_mtl.state.ReviewDraft
import com.example.rnd_transit_mtl.state.ReviewStep
import com.example.rnd_transit_mtl.state.TripActionResult
import com.example.rnd_transit_mtl.ui.TripReviewContent
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitWhite

/**
 * Coordinates review drafts and finishing behavior for one stable trip ID.
 *
 * Reads the latest completed record and draft from LocalTripsStore.
 * Does not keep another mutable Trip or completed collection.
 *
 * onOpenHistory and onReturnToOrigin must synchronously return true
 * only after the destination is established or already present.
 * They must be idempotent so restoration cannot insert duplicate routes.
 *
 * onReturnToOrigin returns to the originating History/details entry.
 * If that origin no longer exists, its navigation owner should use History.
 *
 * isDestinationActive must identify the current entry rather than merely
 * indicate that outgoing content remains composed during a transition.
 */
@Composable
fun TripReviewScreen(
    tripId: String,
    mode: TripReviewMode,
    isDestinationActive: Boolean,
    onOpenHistory: () -> Boolean,
    onReturnToOrigin: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val tripsStore = LocalTripsStore.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val focusManager = LocalFocusManager.current

    val snapshot by tripsStore.state
    val trip = snapshot.completedTrips.firstOrNull { it.id == tripId }
    val draft = snapshot.reviewDrafts[tripId]

    val latestDestinationActive by rememberUpdatedState(
        isDestinationActive
    )
    val latestOpenHistory by rememberUpdatedState(onOpenHistory)
    val latestReturnToOrigin by rememberUpdatedState(onReturnToOrigin)

    var isAppResumed by remember(lifecycle) {
        mutableStateOf(
            lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        )
    }

    var errorMessage by remember(tripId, mode) {
        mutableStateOf<String?>(null)
    }

    /*
     * These are navigation UI state, not another saved review.
     *
     * Once a store operation finishes, restoration must not initialize
     * another draft or apply that operation again while exit is pending.
     */
    var exitMessage by rememberSaveable(tripId, mode.name) {
        mutableStateOf<String?>(null)
    }
    var exitHandled by rememberSaveable(tripId, mode.name) {
        mutableStateOf(false)
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            isAppResumed = lifecycle.currentState
                .isAtLeast(Lifecycle.State.RESUMED)
        }

        lifecycle.addObserver(observer)

        isAppResumed = lifecycle.currentState
            .isAtLeast(Lifecycle.State.RESUMED)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    fun isInteractive(): Boolean =
        latestDestinationActive &&
                lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    fun canEdit(): Boolean =
        isInteractive() && exitMessage == null

    fun finish(message: String) {
        focusManager.clearFocus()
        exitMessage = message
        exitHandled = false
        errorMessage = null
    }

    fun requestExit() {
        if (!isInteractive() || exitMessage == null) return

        val handled = when (mode) {
            TripReviewMode.INITIAL -> latestOpenHistory()
            TripReviewMode.EDIT -> latestReturnToOrigin()
        }

        exitHandled = handled
        errorMessage = if (handled) {
            null
        } else {
            "Navigation has not finished. Your trip is preserved. " +
                    "Use Continue to retry."
        }
    }

    fun discardAndExit() {
        if (!isInteractive()) return

        /*
         * If already finished, Back/Close retries only navigation.
         * It never discards or saves another review.
         */
        if (exitMessage != null) {
            requestExit()
            return
        }

        when (val result = tripsStore.skipReview(tripId)) {
            TripActionResult.Applied -> {
                finish(
                    if (mode == TripReviewMode.INITIAL) {
                        "Review skipped. Your completed trip is preserved."
                    } else {
                        "Changes discarded. Your saved review is preserved."
                    }
                )
            }

            is TripActionResult.MissingTrip -> {
                finish("This trip is no longer available.")
            }

            is TripActionResult.InvalidInput -> {
                errorMessage = result.message
            }

            else -> {
                errorMessage = "The review could not be closed."
            }
        }
    }

    fun updateDraft(transform: (ReviewDraft) -> ReviewDraft) {
        if (!canEdit()) return

        val latestDraft = tripsStore.reviewDrafts[tripId]
            ?: return

        when (
            val result = tripsStore.updateReviewDraft(
                tripId = tripId,
                draft = transform(latestDraft)
            )
        ) {
            TripActionResult.Applied -> errorMessage = null
            is TripActionResult.MissingTrip -> {
                errorMessage = "This trip is no longer available."
            }
            is TripActionResult.InvalidInput -> {
                errorMessage = result.message
            }
            else -> {
                errorMessage = "The review draft could not be updated."
            }
        }
    }

    fun saveAndExit() {
        if (!canEdit()) return

        /*
         * The store resolves the latest record and latest draft by ID.
         * It validates Overall and cannot recreate a deleted trip.
         */
        when (val result = tripsStore.saveReview(tripId)) {
            TripActionResult.Applied -> {
                finish("Review saved.")
            }

            is TripActionResult.MissingTrip -> {
                finish("This trip is no longer available.")
            }

            is TripActionResult.InvalidInput -> {
                errorMessage = result.message
            }

            else -> {
                errorMessage = "The review could not be saved."
            }
        }
    }

    /*
     * beginReview preserves an existing draft after rotation.
     * Without a draft, it copies the latest saved review or starts unrated.
     *
     * A matching pending event can remain after restoration between
     * completion and navigation. This active review destination handles it.
     */
    LaunchedEffect(
        tripsStore,
        tripId,
        mode,
        isDestinationActive,
        trip != null,
        exitMessage
    ) {
        if (
            isDestinationActive &&
            trip != null &&
            exitMessage == null
        ) {
            when (val result = tripsStore.beginReview(tripId)) {
                TripActionResult.Applied -> {
                    if (tripsStore.pendingReviewTripId == tripId) {
                        tripsStore.acknowledgeReviewNavigation(tripId)
                    }
                }

                is TripActionResult.MissingTrip -> {
                    errorMessage = "This trip is no longer available."
                }

                is TripActionResult.InvalidInput -> {
                    errorMessage = result.message
                }

                else -> {
                    errorMessage = "The review could not be opened."
                }
            }
        }
    }

    /*
     * Exit is retried after restoration if it was not acknowledged.
     * A successful exit is not automatically requested again on rotation.
     */
    LaunchedEffect(
        tripId,
        mode,
        isDestinationActive,
        isAppResumed,
        exitMessage,
        exitHandled
    ) {
        if (
            isDestinationActive &&
            isAppResumed &&
            exitMessage != null &&
            !exitHandled
        ) {
            requestExit()
        }
    }

    /*
     * Common Navigation Event API supplied by the existing Navigation 3
     * dependency. This nested handler consumes a completed Back action.
     * Cancelling a predictive gesture makes no draft/store change.
     */
    val backState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )

    NavigationBackHandler(
        state = backState,
        isBackEnabled = isDestinationActive && isAppResumed,
        onBackCompleted = {
            discardAndExit()
        }
    )

    when {
        exitMessage != null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(TransitWhite)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = exitMessage.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TransitMain
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = { requestExit() },
                    enabled = isDestinationActive && isAppResumed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (mode == TripReviewMode.INITIAL) {
                            "Continue to History"
                        } else {
                            "Return to previous view"
                        }
                    )
                }
            }
        }

        trip == null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(TransitWhite)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Trip unavailable",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TransitMain
                )

                Text(
                    text = "This completed trip was removed or is not " +
                            "available in the current session.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TransitMain
                )

                Button(
                    onClick = {
                        if (isInteractive()) {
                            finish("This trip is no longer available.")
                        }
                    },
                    enabled = isDestinationActive && isAppResumed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return")
                }
            }
        }

        draft == null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(TransitWhite)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Opening review",
                    style = MaterialTheme.typography.titleLarge,
                    color = TransitMain
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = { discardAndExit() },
                    enabled = isDestinationActive && isAppResumed
                ) {
                    Text("Close")
                }
            }
        }

        else -> {
            TripReviewContent(
                tripTitle = trip.title,
                mode = mode,
                draft = draft,
                controlsEnabled = isDestinationActive && isAppResumed,
                errorMessage = errorMessage,
                onRatingChange = { question, rating ->
                    updateDraft { latest ->
                        /*
                         * Ignore callbacks from an outgoing animated panel.
                         */
                        if (latest.step == question) {
                            latest.withRating(question, rating)
                        } else {
                            latest
                        }
                    }
                },
                onCommentChange = { comment ->
                    updateDraft { latest ->
                        if (latest.step == ReviewStep.OVERALL) {
                            latest.copy(comment = comment)
                        } else {
                            latest
                        }
                    }
                },
                onStepChange = { requestedStep ->
                    updateDraft { latest ->
                        val adjacent =
                            requestedStep == latest.step.previousOrNull() ||
                                    requestedStep == latest.step.nextOrNull()

                        val movingForward =
                            requestedStep.ordinal > latest.step.ordinal

                        if (
                            adjacent &&
                            (!movingForward || latest.overall != null)
                        ) {
                            latest.copy(step = requestedStep)
                        } else {
                            latest
                        }
                    }
                },
                onSave = { saveAndExit() },
                onDiscard = { discardAndExit() },
                modifier = modifier
            )
        }
    }
}