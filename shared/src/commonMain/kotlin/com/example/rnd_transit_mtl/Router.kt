package com.example.rnd_transit_mtl

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.model.TripReviewMode
import com.example.rnd_transit_mtl.state.LocalTripsStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Restricted shared destinations with titles for MainLayout's header.
 */
@Serializable
sealed class ScreenKey : NavKey {
    abstract val screenTitle: String
}

@Serializable
data object MainScreenKey : ScreenKey() {
    override val screenTitle = "Home"
}

@Serializable
data object ProfileScreenKey : ScreenKey() {
    override val screenTitle = "user"
}

@Serializable
data object AboutScreenKey : ScreenKey() {
    override val screenTitle = "about"
}

@Serializable
data object SettingsScreenKey : ScreenKey() {
    override val screenTitle = "settings"
}

@Serializable
data object HistoryScreenKey : ScreenKey() {
    override val screenTitle = "history"
}

/**
 * Carries the generated record into the second content screen.
 *
 * CurrentTripScreen resolves runtime state using this record's stable ID.
 * The parameter cannot recreate a missing trip.
 */
@Serializable
data class CurrentTripScreenKey(
    val trip: Trip
) : ScreenKey() {
    override val screenTitle = "Current trip"
}

/**
 * Review coordination uses the stable ID and explicit initial/edit behavior.
 */
@Serializable
data class TripReviewScreenKey(
    val tripId: String,
    val mode: TripReviewMode
) : ScreenKey() {
    override val screenTitle = "Rate your trip"
}

/**
 * Explicit registration remains necessary for the NavKey back-stack serializer.
 */
val backStackConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MainScreenKey::class, MainScreenKey.serializer())
            subclass(ProfileScreenKey::class, ProfileScreenKey.serializer())
            subclass(AboutScreenKey::class, AboutScreenKey.serializer())
            subclass(SettingsScreenKey::class, SettingsScreenKey.serializer())
            subclass(HistoryScreenKey::class, HistoryScreenKey.serializer())
            subclass(
                CurrentTripScreenKey::class,
                CurrentTripScreenKey.serializer()
            )
            subclass(
                TripReviewScreenKey::class,
                TripReviewScreenKey.serializer()
            )
        }
    }
}

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No Navigator found! Wrap your UI with CompositionLocalProvider.")
}

/**
 * Owns destination changes while screens coordinate store operations.
 *
 * Destination-active flags are computed from the actual stack top.
 * Outgoing content cannot continue the simulation during transitions.
 */
@Composable
fun Router(
    backStack: NavBackStack<NavKey>,
    transportTypes: List<TransportType>?,
    transportRoutes: List<TransportRoute>?,
    loadingError: Boolean
) {
    val navigator = LocalNavigator.current
    val tripsStore = LocalTripsStore.current

    fun returnToPlanner(): Boolean {
        if (MainScreenKey !in backStack) return false
        navigator.popUntil(MainScreenKey)
        return navigator.current == MainScreenKey
    }

    fun openHistory(): Boolean {
        if (navigator.current == HistoryScreenKey) return true

        val existingHistory = backStack.lastOrNull {
            it == HistoryScreenKey
        }

        if (existingHistory != null) {
            navigator.popUntil(existingHistory)
        } else {
            if (!returnToPlanner()) return false
            navigator.navigate(HistoryScreenKey)
        }

        return navigator.current == HistoryScreenKey
    }

    fun openInitialReview(tripId: String): Boolean {
        if (tripsStore.findCompleted(tripId) == null) return false

        val destination = TripReviewScreenKey(
            tripId = tripId,
            mode = TripReviewMode.INITIAL
        )
        val current = navigator.current

        if (current == destination) return true

        val fromCurrentTrip =
            current is CurrentTripScreenKey &&
                    current.trip.id == tripId

        val fromPendingAction =
            tripsStore.pendingReviewTripId == tripId &&
                    (current == MainScreenKey ||
                            current == HistoryScreenKey)

        if (!fromCurrentTrip && !fromPendingAction) return false

        val existingDestination = backStack.lastOrNull {
            it == destination
        }

        when {
            existingDestination != null -> {
                navigator.popUntil(existingDestination)
            }

            fromCurrentTrip -> {
                // Remove the finished simulation entry from the top.
                navigator.replace(destination)
            }

            else -> {
                navigator.navigate(destination)
            }
        }

        // The caller acknowledges the pending event after this succeeds.
        return navigator.current == destination
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = {
            val current = navigator.current

            if (current is TripReviewScreenKey) {
                // Fallback for Back events not consumed by the review screen.
                // skipReview also discards an edit draft without changing
                // an existing saved review.
                tripsStore.skipReview(current.tripId)

                if (current.mode == TripReviewMode.INITIAL) {
                    openHistory()
                } else {
                    navigator.pop()
                }
            } else {
                navigator.pop()
            }
        },
        entryProvider = entryProvider {
            entry<MainScreenKey> {
                MainScreen(
                    transportTypes = transportTypes,
                    transportRoutes = transportRoutes,
                    loadingError = loadingError,
                    isDestinationActive =
                        navigator.current == MainScreenKey,
                    onOpenCurrentTrip = { trip ->
                        val storedActive = tripsStore.activeTrip

                        if (
                            navigator.current != MainScreenKey ||
                            storedActive == null ||
                            storedActive.id != trip.id
                        ) {
                            false
                        } else {
                            val existing = backStack
                                .filterIsInstance<CurrentTripScreenKey>()
                                .lastOrNull {
                                    it.trip.id == storedActive.id
                                }

                            if (existing != null) {
                                navigator.popUntil(existing)
                            } else {
                                navigator.navigate(
                                    CurrentTripScreenKey(storedActive)
                                )
                            }

                            val current = navigator.current
                            current is CurrentTripScreenKey &&
                                    current.trip.id == storedActive.id
                        }
                    },
                    onOpenPendingReview = { tripId ->
                        if (navigator.current == MainScreenKey) {
                            openInitialReview(tripId)
                        } else {
                            false
                        }
                    }
                )
            }

            entry<CurrentTripScreenKey> { key ->
                CurrentTripScreen(
                    trip = key.trip,
                    isDestinationActive = navigator.current == key,
                    onReviewRequested = { tripId ->
                        openInitialReview(tripId)
                    },
                    onLeave = {
                        if (navigator.current == key) {
                            returnToPlanner()
                        }
                    },
                    onReturnToPlanner = {
                        if (navigator.current == key) {
                            returnToPlanner()
                        }
                    }
                )
            }

            entry<TripReviewScreenKey> { key ->
                TripReviewScreen(
                    tripId = key.tripId,
                    mode = key.mode,
                    isDestinationActive = navigator.current == key,
                    onOpenHistory = {
                        when {
                            navigator.current == HistoryScreenKey -> true
                            navigator.current == key -> openHistory()
                            else -> false
                        }
                    },
                    onReturnToOrigin = {
                        when {
                            navigator.current == HistoryScreenKey -> true

                            navigator.current == key -> {
                                if (navigator.hasPrevious()) {
                                    navigator.pop()
                                    true
                                } else {
                                    openHistory()
                                }
                            }

                            else -> false
                        }
                    }
                )
            }

            entry<HistoryScreenKey> {
                HistoryScreen(
                    isDestinationActive =
                        navigator.current == HistoryScreenKey,
                    onReview = { tripId ->
                        if (
                            navigator.current == HistoryScreenKey &&
                            tripsStore.findCompleted(tripId) != null
                        ) {
                            if (tripsStore.pendingReviewTripId == tripId) {
                                if (openInitialReview(tripId)) {
                                    tripsStore.acknowledgeReviewNavigation(
                                        tripId
                                    )
                                }
                            } else {
                                navigator.navigate(
                                    TripReviewScreenKey(
                                        tripId = tripId,
                                        mode = TripReviewMode.EDIT
                                    )
                                )
                            }
                        }
                    }
                )
            }

            entry<ProfileScreenKey> {
                ProfileScreen()
            }

            entry<AboutScreenKey> {
                AboutScreen()
            }

            entry<SettingsScreenKey> {
                SettingsScreen()
            }
        },
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        }
    )
}
