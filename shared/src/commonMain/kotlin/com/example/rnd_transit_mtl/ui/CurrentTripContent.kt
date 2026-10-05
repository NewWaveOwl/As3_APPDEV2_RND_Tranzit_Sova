package com.example.rnd_transit_mtl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitWhite

/** Map and compact progress footer; trip information appears only in a popup. */
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
    modifier: Modifier = Modifier,
    showTripInformation: Boolean = false,
    onDismissInformation: () -> Unit = {},
    mapInteractive: Boolean = false,
    onShowInformation: () -> Unit = {},
    animateEntrance: Boolean = false
) {
    val boundedProgress = boundedTripProgress(progress)
    val completed = boundedProgress == 1f
    val status = when {
        completed -> "Destination reached"
        !isRunning -> "Trip paused"
        else -> "Following the mock route"
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(TransitMain)
    ) {
        val availableHeight = maxHeight
        // Both screens use the whole available map viewport beneath the shared bar.
        MockTripMap(
            start = trip.start,
            destination = trip.destination,
            progress = boundedProgress,
            interactive = mapInteractive && actionsEnabled,
            matchPlannerBackground = true,
            controlsTopPadding = 66.dp,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "Current trip",
            textAlign = TextAlign.End,
            color = TransitWhite,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.TopCenter)
                .fillMaxWidth().height(56.dp)
                .background(TransitMain)
                .clickable(enabled = actionsEnabled, role = Role.Button, onClick = onShowInformation)
                .semantics { contentDescription = "Current trip. Open trip information." }
                .padding(horizontal = 20.dp, vertical = 12.dp)
        )

        var footerVisible by remember(trip.id) { mutableStateOf(!animateEntrance) }
        LaunchedEffect(trip.id, animateEntrance) { footerVisible = true }
        AnimatedVisibility(
            visible = footerVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(400), initialOffsetY = { it }) +
                fadeIn(tween(400))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(max = availableHeight * 0.45f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (message != null) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                            .background(TransitWhite).padding(12.dp)
                    )
                }
                TripProgressPanel(
                    tripTitle = trip.title,
                    destinationLabel = trip.destination.label,
                    distanceKm = trip.distanceKm,
                    progress = boundedProgress,
                    statusText = status,
                    onCompletedClick = onRequestReview,
                    actionsEnabled = actionsEnabled
                )
            }
        }

        if (showTripInformation) {
            AlertDialog(
                onDismissRequest = onDismissInformation,
                containerColor = TransitWhite,
                titleContentColor = TransitMain,
                textContentColor = TransitMain,
                title = {
                    Text(trip.title, style = MaterialTheme.typography.titleLarge)
                },
                text = {
                    Column(
                        modifier = Modifier.heightIn(max = availableHeight * 0.6f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${formatDemoDistanceKm(trip.distanceKm)} km demo",
                            color = TransitMain
                        )
                        TripInformationPanel(
                            trip = trip,
                            onOpenImageReference = onOpenImageReference
                        )
                        if (!completed) {
                            TextButton(
                                onClick = onCancel,
                                enabled = actionsEnabled
                            ) {
                                Text("Cancel trip", color = TransitMain)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismissInformation) {
                        Text("Close", color = TransitMain)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onLeave, enabled = actionsEnabled) {
                        Text("Return to GO", color = TransitMain)
                    }
                }
            )
        }
    }
}
