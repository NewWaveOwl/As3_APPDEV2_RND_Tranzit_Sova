package com.example.rnd_transit_mtl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.rnd_transit_mtl.data.FakeTransportRouteRepository
import com.example.rnd_transit_mtl.data.FakeTransportTypeRepository
import com.example.rnd_transit_mtl.layout.MainLayout
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.state.LocalTripsStore
import com.example.rnd_transit_mtl.state.TripsStore
import com.example.rnd_transit_mtl.ui.theme.RNDTransitTheme
import kotlinx.coroutines.CancellationException

/**
 * Keeps successfully loaded transport types and routes together.
 *
 * Publishing one TransportData value makes both lists available
 * to the UI together.
 */
private data class TransportData(
    val types: List<TransportType>,
    val routes: List<TransportRoute>
)

/**
 * Owns the shared navigation stack and restorable TripsStore.
 *
 * Transport resources are reloaded when App enters composition.
 * TripsStore restores records, active elapsed time, pending review,
 * and review drafts through its explicit String Saver.
 *
 * The shared theme and MainLayout remain above Router.
 */
@Composable
fun App() {
    val backStack = rememberNavBackStack(
        backStackConfig,
        MainScreenKey
    )
    val navigator = remember(backStack) {
        Navigator(backStack)
    }

    val tripsStore = rememberSaveable(
        saver = TripsStore.Saver
    ) {
        TripsStore()
    }

    var transportData by remember {
        mutableStateOf<TransportData?>(null)
    }
    var loadingError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        try {
            transportData = TransportData(
                types = FakeTransportTypeRepository()
                    .getTransportTypes(),
                routes = FakeTransportRouteRepository()
                    .getTransportRoutes()
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            loadingError = true
        }
    }

    RNDTransitTheme {
        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalTripsStore provides tripsStore
        ) {
            MainLayout {
                Router(
                    backStack,
                    transportData?.types,
                    transportData?.routes,
                    loadingError
                )
            }
        }
    }
}