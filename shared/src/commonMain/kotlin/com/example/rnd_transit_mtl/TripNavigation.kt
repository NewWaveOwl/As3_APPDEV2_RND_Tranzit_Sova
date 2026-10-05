package com.example.rnd_transit_mtl

import androidx.compose.runtime.compositionLocalOf
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.model.TripReviewMode
import com.example.rnd_transit_mtl.state.TripActionResult
import com.example.rnd_transit_mtl.state.TripsStore

typealias RegisterReviewBackHandler =
        (() -> Unit) -> (() -> Unit)

val LocalTripNavigation = compositionLocalOf<TripNavigation> {
    error("TripNavigation must be provided by App.")
}

/**
 * Coordinates route changes using one Navigator and one TripsStore.
 *
 * Records and drafts remain in TripsStore. Origins remain in route keys.
 * The live Back callback is temporary and is never saved.
 */
class TripNavigation(
    private val navigator: Navigator,
    private val tripsStore: TripsStore
) {
    private class ReviewBackRegistration(
        val key: TripReviewScreenKey,
        val onBack: () -> Unit
    )

    private var reviewBackRegistration: ReviewBackRegistration? = null

    /**
     * Lets header Back invoke the same close operation as platform Back.
     *
     * Identity checking prevents an outgoing entry's disposal from removing
     * a newer registration for the same key.
     */
    fun registerReviewBackHandler(
        key: TripReviewScreenKey,
        onBack: () -> Unit
    ): () -> Unit {
        val registration = ReviewBackRegistration(key, onBack)
        reviewBackRegistration = registration

        return {
            if (reviewBackRegistration === registration) {
                reviewBackRegistration = null
            }
        }
    }

    fun openCurrentTrip(trip: Trip): Boolean {
        val active = tripsStore.activeTrip ?: return false
        if (active.id != trip.id) return false
        if (!discardCurrentReview()) return false

        pruneCompletedSimulationEntries()

        val existing = navigator.findLast {
            it is CurrentTripScreenKey && it.trip.id == trip.id
        }

        if (existing != null) {
            navigator.popUntil(existing)
        } else {
            // The generated item is passed as the navigation parameter.
            navigator.navigate(CurrentTripScreenKey(trip))
        }

        val current = navigator.current
        return current is CurrentTripScreenKey &&
                current.trip.id == trip.id
    }

    fun resumeActiveTrip(): Boolean {
        val trip = tripsStore.activeTrip ?: return false
        return openCurrentTrip(trip)
    }

    /**
     * Establishes the matching initial review, then acknowledges the event.
     * Repeated requests recognize the existing destination.
     */
    fun openInitialReview(tripId: String): Boolean {
        if (tripsStore.findCompleted(tripId) == null) return false

        val destination = TripReviewScreenKey(
            tripId = tripId,
            mode = TripReviewMode.INITIAL
        )
        val current = navigator.current

        if (current == destination) {
            return acknowledgeMatchingPendingReview(tripId)
        }

        val fromMatchingTrip =
            current is CurrentTripScreenKey &&
                    current.trip.id == tripId

        val fromPendingEvent =
            tripsStore.pendingReviewTripId == tripId

        if (!fromMatchingTrip && !fromPendingEvent) return false

        // Preserve an unrelated review until its user finishes or leaves it.
        if (
            current is TripReviewScreenKey &&
            current.tripId != tripId
        ) {
            return false
        }

        val existing = navigator.findLast {
            it == destination
        }

        when {
            existing != null -> navigator.popUntil(existing)
            fromMatchingTrip -> navigator.replace(destination)
            else -> navigator.navigate(destination)
        }

        pruneCompletedSimulationEntries()

        if (navigator.current != destination) return false
        return acknowledgeMatchingPendingReview(tripId)
    }

    fun recoverPendingReview(): Boolean {
        val tripId = tripsStore.pendingReviewTripId ?: return false
        val current = navigator.current

        if (
            current is TripReviewScreenKey &&
            (
                current.tripId != tripId ||
                        current.mode != TripReviewMode.INITIAL
                )
        ) {
            return false
        }

        return openInitialReview(tripId)
    }

    fun openDetails(tripId: String): Boolean {
        if (tripsStore.findCompleted(tripId) == null) return false
        if (!discardCurrentReview()) return false

        pruneCompletedSimulationEntries()
        val destination = TripDetailsScreenKey(tripId)
        navigator.open(destination)
        return navigator.current == destination
    }

    fun openReview(
        tripId: String,
        origin: ReviewOrigin
    ): Boolean {
        if (tripsStore.findCompleted(tripId) == null) return false

        if (tripsStore.pendingReviewTripId == tripId) {
            return openInitialReview(tripId)
        }

        val destination = TripReviewScreenKey(
            tripId = tripId,
            mode = TripReviewMode.EDIT,
            origin = origin
        )

        if (navigator.current == destination) return true
        if (navigator.current != origin.destination()) return false

        navigator.open(destination)
        return navigator.current == destination
    }

    /**
     * Called after the screen has saved or discarded its draft.
     *
     * Initial review normally replaces itself with History.
     * Editing returns to the saved origin, recovering to History if deleted.
     */
    fun finishReview(key: TripReviewScreenKey): Boolean {
        val target = reviewReturnDestination(key)

        if (navigator.current != key) {
            return navigator.current == target
        }

        pruneCompletedSimulationEntries()
        replaceOrReturn(target)
        return navigator.current == target
    }

    /**
     * Header section shortcuts close an unsaved review before switching.
     * Existing saved feedback and completed records are preserved.
     */
    fun openSection(destination: ScreenKey): Boolean {
        when (destination) {
            MainScreenKey,
            HistoryScreenKey,
            ProfileScreenKey,
            SettingsScreenKey,
            AboutScreenKey -> Unit
            else -> return false
        }

        if (!discardCurrentReview()) return false

        pruneCompletedSimulationEntries()
        navigator.open(destination)
        return navigator.current == destination
    }

    fun returnToPlanner(): Boolean =
        openSection(MainScreenKey)

    /**
     * Header Back and NavDisplay's platform Back fallback use this operation.
     *
     * A composed review registers its own close handler. Before registration,
     * the fallback performs the same discard-and-return behavior.
     */
    fun back(): Boolean {
        if (!navigator.hasPrevious()) return false

        val current = navigator.current
        if (current is TripReviewScreenKey) {
            val registration = reviewBackRegistration

            if (registration?.key == current) {
                registration.onBack()
                return true
            }

            return when (tripsStore.skipReview(current.tripId)) {
                TripActionResult.Applied,
                is TripActionResult.MissingTrip -> finishReview(current)
                else -> false
            }
        }

        /*
         * Handles a Back tap at the completion boundary before the review
         * destination has been established. The completed record is retained.
         */
        if (
            current is CurrentTripScreenKey &&
            tripsStore.findCompleted(current.trip.id) != null
        ) {
            tripsStore.skipReview(current.trip.id)
            pruneCompletedSimulationEntries()
            replaceOrReturn(HistoryScreenKey)
            return navigator.current == HistoryScreenKey
        }

        pruneCompletedSimulationEntries()
        navigator.pop()
        return true
    }

    private fun reviewReturnDestination(
        key: TripReviewScreenKey
    ): ScreenKey {
        if (key.mode == TripReviewMode.INITIAL) {
            return HistoryScreenKey
        }

        val origin = key.origin
        return if (
            origin is ReviewOrigin.Details &&
            tripsStore.findCompleted(origin.tripId) == null
        ) {
            HistoryScreenKey
        } else {
            origin.destination()
        }
    }

    private fun replaceOrReturn(destination: ScreenKey) {
        if (navigator.findLast { it == destination } != null) {
            navigator.popUntil(destination)
        } else {
            navigator.replace(destination)
        }
    }

    private fun discardCurrentReview(): Boolean {
        val key = navigator.current as? TripReviewScreenKey
            ?: return true

        return when (tripsStore.skipReview(key.tripId)) {
            TripActionResult.Applied,
            is TripActionResult.MissingTrip -> true
            else -> false
        }
    }

    private fun acknowledgeMatchingPendingReview(
        tripId: String
    ): Boolean {
        if (tripsStore.pendingReviewTripId != tripId) return true

        return tripsStore.acknowledgeReviewNavigation(tripId) ==
                TripActionResult.Applied
    }

    private fun pruneCompletedSimulationEntries() {
        navigator.removeWhere { key ->
            key is CurrentTripScreenKey &&
                    tripsStore.findCompleted(key.trip.id) != null
        }
    }
}
