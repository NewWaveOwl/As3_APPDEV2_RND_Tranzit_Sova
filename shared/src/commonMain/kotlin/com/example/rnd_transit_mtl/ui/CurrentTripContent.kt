package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitWhite

/**
 * Stateless active-trip presentation.
 *
 * Previews supply fixed progress and callbacks.
 * Runtime state, lifecycle, timers, and navigation belong to the screen.
 *
 * The compact teal heading, raised status card, yellow footer,
 * and pill-shaped track follow the supplied mockup's layout.
 */
@Composable
fun CurrentTripContent(
    trip: Trip,
    progress: Float,
    isRunning: Boolean,
    actionsEnabled: Boolean,
    message: String?,
    onLeave: () -> Unit,
    onCancel: () -> Unit,
    onRequestReview: () -> Unit,
    onOpenImageReference: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boundedProgress = boundedTripProgress(progress)
    val completed = boundedProgress == 1f

    val status = when {
        completed -> "Demo destination reached"
        !isRunning -> "Demo paused"
        boundedProgress == 0f -> "Starting the mock route"
        else -> "Travelling toward ${trip.destination.label}"
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TransitWhite)
    ) {
        val wideLayout = maxWidth >= 700.dp

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 820.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = if (wideLayout) 24.dp else 0.dp,
                    vertical = if (wideLayout) 16.dp else 0.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TransitMain)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TransitWhite
                )

                Text(
                    text = "${formatDemoDistanceKm(trip.distanceKm)} km demo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TransitWhite
                )
            }

            MockTripMap(
                start = trip.start,
                destination = trip.destination,
                progress = boundedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (wideLayout) 360.dp else 280.dp)
            )

            TripProgressPanel(
                tripTitle = trip.title,
                destinationLabel = trip.destination.label,
                distanceKm = trip.distanceKm,
                progress = boundedProgress,
                statusText = status
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (completed) {
                    Button(
                        onClick = onRequestReview,
                        enabled = actionsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (trip.review == null) {
                                "Open trip review"
                            } else {
                                "View or edit review"
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = onLeave,
                        enabled = actionsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Return")
                    }
                } else {
                    OutlinedButton(
                        onClick = onLeave,
                        enabled = actionsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pause and return")
                    }

                    Button(
                        onClick = onCancel,
                        enabled = actionsEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel trip")
                    }
                }
            }

            TripInformationPanel(
                trip = trip,
                onOpenImageReference = onOpenImageReference,
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
            )
        }
    }
}
