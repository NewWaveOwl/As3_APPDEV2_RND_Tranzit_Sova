package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.TransportRoute
import com.example.rnd_transit_mtl.model.TransportType
import com.example.rnd_transit_mtl.ui.theme.TransitComplementary
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitWhite
import org.jetbrains.compose.resources.painterResource
import rnd_transit_mtl.shared.generated.resources.Res
import rnd_transit_mtl.shared.generated.resources.map_sample

/**
 * Stateless planner presentation.
 *
 * All entered values, selections, validation, expansion, and action decisions
 * belong to TransitOpeningScreen. Only the scroll position is local UI state.
 */
@Composable
fun TripPlannerContent(
    tripTitle: String,
    onTripTitleChange: (String) -> Unit,
    tripDescription: String,
    onTripDescriptionChange: (String) -> Unit,
    imageUrl: String,
    onImageUrlChange: (String) -> Unit,
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
    titleError: String?,
    descriptionError: String?,
    imageUrlError: String?,
    validationMessage: String,
    actionLabel: String,
    actionEnabled: Boolean,
    inputsEnabled: Boolean,
    activeTripSummary: String?,
    pendingReviewSummary: String?,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TransitMain),
        contentAlignment = Alignment.TopCenter
    ) {
        val layoutScale = (
                maxWidth.value.coerceAtMost(430f) / 402f
                ).coerceIn(0.7f, 1.1f)

        Image(
            painter = painterResource(Res.drawable.map_sample),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .widthIn(max = 620.dp)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        TransitWhite,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Plan your trip",
                    color = TransitMain,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Choose your trip information and preferences. " +
                            "The mock journey runs for 10 seconds of active time.",
                    color = TransitMain,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (activeTripSummary != null) {
                    Text(
                        text = activeTripSummary,
                        color = TransitMain,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Resume keeps the same trip and progress. " +
                                "Changes below are a draft for your next trip.",
                        color = TransitMain,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (pendingReviewSummary != null) {
                    Text(
                        text = pendingReviewSummary,
                        color = TransitMain,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Save or skip that review before starting another trip.",
                        color = TransitMain,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TransitMain,
                    unfocusedTextColor = TransitMain,
                    disabledTextColor = TransitMain.copy(alpha = 0.65f),
                    errorTextColor = TransitMain,
                    focusedContainerColor = TransitWhite,
                    unfocusedContainerColor = TransitWhite,
                    disabledContainerColor = TransitWhite,
                    errorContainerColor = TransitWhite,
                    cursorColor = TransitMain,
                    focusedBorderColor = TransitMain,
                    unfocusedBorderColor = TransitComplementary,
                    focusedLabelColor = TransitMain,
                    unfocusedLabelColor = TransitMain,
                    focusedSupportingTextColor = TransitMain,
                    unfocusedSupportingTextColor = TransitMain
                )

                OutlinedTextField(
                    value = tripTitle,
                    onValueChange = onTripTitleChange,
                    label = { Text("Trip title") },
                    singleLine = true,
                    enabled = inputsEnabled,
                    isError = titleError != null,
                    supportingText = {
                        Text(titleError ?: "A short name to recognize in History.")
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tripDescription,
                    onValueChange = onTripDescriptionChange,
                    label = { Text("Trip description") },
                    minLines = 3,
                    maxLines = 5,
                    enabled = inputsEnabled,
                    isError = descriptionError != null,
                    supportingText = {
                        Text(
                            descriptionError
                                ?: "Describe what you would like to discover."
                        )
                    },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChange,
                    label = { Text("Image URL") },
                    singleLine = true,
                    enabled = inputsEnabled,
                    isError = imageUrlError != null,
                    supportingText = {
                        Text(
                            imageUrlError
                                ?: "Direct HTTPS image link. The map stays bundled."
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp * layoutScale)
            )

            if (validationMessage.isNotEmpty()) {
                Text(
                    text = validationMessage,
                    color = TransitMain,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            TransitWhite,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                )
            }

            GOBox(
                minutes = minutes,
                onMinutesChange = onMinutesChange,
                onGo = onPrimaryAction,
                layoutScale = layoutScale,
                actionLabel = actionLabel,
                actionEnabled = actionEnabled,
                minutesEnabled = inputsEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
