package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.ui.theme.TransitComplementary
import com.example.rnd_transit_mtl.ui.theme.TransitHighlight
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitSelected
import com.example.rnd_transit_mtl.ui.theme.TransitWhite

/**
 * Stateless progress presentation for the mock trip.
 *
 * distanceKm is the stored total demo distance. Remaining distance,
 * track fill, and percentage derive from the same supplied progress.
 *
 * The wording describes a straight-line simulation and does not
 * invent geographic directions or street turns.
 */
@Composable
fun TripProgressPanel(
    tripTitle: String,
    destinationLabel: String,
    distanceKm: Double,
    progress: Float,
    modifier: Modifier = Modifier
) {
    require(tripTitle.isNotBlank()) {
        "Trip title must not be blank."
    }
    require(destinationLabel.isNotBlank()) {
        "Destination label must not be blank."
    }
    require(distanceKm.isFinite() && distanceKm > 0.0) {
        "Total demo distance must be finite and positive."
    }

    val boundedProgress = boundedTripProgress(progress)
    val percentage = tripProgressPercentage(boundedProgress)
    val remainingDistanceKm =
        distanceKm * (1.0 - boundedProgress.toDouble())

    val totalText = "${formatDemoDistanceKm(distanceKm)} km"
    val remainingText =
        "${formatDemoDistanceKm(remainingDistanceKm)} km"

    val status = when (boundedProgress) {
        0f -> "Ready at the start"
        1f -> "Demo destination reached"
        else -> "Following the mock route"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                TransitMain,
                RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = TransitComplementary,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = tripTitle,
            style = MaterialTheme.typography.titleLarge,
            color = TransitWhite
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Destination: $destinationLabel",
                style = MaterialTheme.typography.bodyLarge,
                color = TransitWhite
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = TransitSelected
            )
        }

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth < 380.dp) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Total demo distance: $totalText",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TransitHighlight
                    )
                    Text(
                        text = "Remaining demo distance: $remainingText",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TransitWhite
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Total demo distance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TransitWhite
                        )
                        Text(
                            text = totalText,
                            style = MaterialTheme.typography.titleLarge,
                            color = TransitHighlight
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Remaining demo distance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TransitWhite
                        )
                        Text(
                            text = remainingText,
                            style = MaterialTheme.typography.titleLarge,
                            color = TransitWhite
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Demo progress",
                style = MaterialTheme.typography.labelLarge,
                color = TransitWhite
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                color = TransitHighlight
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TransitWhite)
                .semantics {
                    contentDescription = "Trip demo progress"
                    stateDescription = "$percentage percent"
                    progressBarRangeInfo = ProgressBarRangeInfo(
                        current = boundedProgress,
                        range = 0f..1f
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(boundedProgress)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(boundedProgress)
                    .height(16.dp)
                    .background(TransitSelected)
            )
        }

        Text(
            text = "Straight-line mock route on a bundled image.",
            style = MaterialTheme.typography.bodySmall,
            color = TransitWhite
        )
    }
}