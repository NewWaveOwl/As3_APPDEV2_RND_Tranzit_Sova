package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitWhite
import org.jetbrains.compose.resources.painterResource
import rnd_transit_mtl.shared.generated.resources.Res
import rnd_transit_mtl.shared.generated.resources.map_sample

/** Stateless map-first planner. Selections and GO decisions belong to the parent. */
@Composable
fun TripPlannerContent(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    transportTypes: List<TransportType>,
    transportRoutes: List<TransportRoute>,
    selectedTransportIds: List<String>,
    selectedRouteIds: List<String>,
    expandedTransportId: String?,
    onExpandedTransportChange: (String) -> Unit,
    onToggleTransport: (String) -> Unit,
    onToggleRoute: (String, String) -> Unit,
    intensity: Float,
    onIntensityChange: (Float) -> Unit,
    validationMessage: String,
    actionLabel: String,
    actionEnabled: Boolean,
    activeTripSummary: String?,
    pendingReviewSummary: String?,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize().background(TransitMain),
        contentAlignment = Alignment.TopCenter
    ) {
        val availableHeight = maxHeight
        val layoutScale = (maxWidth.value.coerceAtMost(430f) / 402f)
            .coerceIn(0.7f, 1.1f)
        val controlHeight = 96.dp * layoutScale
        // GO is centered in the available screen. Expanded choices remain scrollable.
        val topSpace = ((availableHeight - controlHeight) / 2f).coerceAtLeast(24.dp)

        Image(
            painter = painterResource(Res.drawable.map_sample),
            contentDescription = "Bundled mock transit map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(topSpace))

            GOBox(
                minutes = minutes,
                onMinutesChange = onMinutesChange,
                onGo = onPrimaryAction,
                layoutScale = layoutScale,
                actionLabel = actionLabel,
                actionEnabled = actionEnabled,
                minutesEnabled = actionEnabled,
                modifier = Modifier.fillMaxWidth(0.92f)
            )

            val summary = activeTripSummary ?: pendingReviewSummary
            if (summary != null || validationMessage.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .background(TransitWhite, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (summary != null) {
                        Text(summary, color = TransitMain,
                            style = MaterialTheme.typography.bodyMedium)
                    }
                    if (validationMessage.isNotEmpty()) {
                        Text(
                            text = validationMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(12.dp))
            }

            // Teal covers all remaining space, including below the yellow panel.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = (availableHeight - topSpace - controlHeight - 12.dp)
                            .coerceAtLeast(0.dp)
                    )
                    .background(TransitMain)
            ) {
                TransportPanel(
                    layoutScale = layoutScale,
                    transportTypes = transportTypes,
                    transportRoutes = transportRoutes,
                    selectedTransportIds = selectedTransportIds,
                    selectedRouteIds = selectedRouteIds,
                    expandedTransportId = expandedTransportId,
                    onExpandedTransportChange = onExpandedTransportChange,
                    onToggleTransport = onToggleTransport,
                    onToggleRoute = onToggleRoute,
                    modifier = Modifier.fillMaxWidth()
                )

                IntensityPanel(
                    layoutScale = layoutScale,
                    intensity = intensity,
                    onIntensityChange = onIntensityChange,
                    validationMessage = "",
                    modifier = Modifier.fillMaxWidth().height(120.dp * layoutScale)
                )
            }
        }
    }
}
