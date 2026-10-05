package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rnd_transit_mtl.model.Trip
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitSelected
import com.example.rnd_transit_mtl.ui.theme.TransitWhite
import org.jetbrains.compose.resources.painterResource
import rnd_transit_mtl.shared.generated.resources.Res
import rnd_transit_mtl.shared.generated.resources.ic_arrow_drop_down
import rnd_transit_mtl.shared.generated.resources.ic_arrow_drop_up
import kotlin.math.abs

/**
 * Stateless planned-minutes selector and primary planner action.
 *
 * The parent decides whether the action starts, resumes, or opens a completed trip.
 * Selected minutes do not determine the ten-second simulation duration.
 */
@Composable
internal fun GOBox(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier = Modifier,
    layoutScale: Float = 1f,
    actionLabel: String = "GO",
    actionEnabled: Boolean = true,
    minutesEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .height(96.dp * layoutScale)
            .background(
                Brush.horizontalGradient(
                    0f to TransitMain,
                    0.84f to TransitMain,
                    1f to TransitSelected
                ),
                RoundedCornerShape(28.dp * layoutScale)
            )
            .padding(horizontal = 8.dp * layoutScale),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("I have", color = TransitWhite, fontSize = 20.sp * layoutScale,
            maxLines = 1, softWrap = false)
        ControlDivider(layoutScale)
        ScrollableMinutes(minutes, onMinutesChange, minutesEnabled, layoutScale)
        ControlDivider(layoutScale)
        Text("minutes", color = TransitWhite, fontSize = 20.sp * layoutScale,
            maxLines = 1, softWrap = false)
        ControlDivider(layoutScale)
        Box(
            modifier = Modifier
                .width(if (actionLabel == "Resume trip") 84.dp else 60.dp)
                .fillMaxHeight()
                .clickable(enabled = actionEnabled, onClick = onGo)
                .semantics { contentDescription = actionLabel },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = actionLabel,
                color = TransitWhite.copy(alpha = if (actionEnabled) 1f else 0.5f),
                fontSize = (if (actionLabel == "Resume trip") 16.sp else 22.sp) * layoutScale,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ControlDivider(layoutScale: Float) {
    Spacer(
        Modifier.width(1.dp).height(58.dp * layoutScale)
            .background(TransitWhite.copy(alpha = 0.8f))
    )
}

/**
 * Arrow taps and vertical dragging change minutes in five-minute steps.
 *
 * Updated-state references let a continuous gesture keep using the latest
 * value/callback without restarting its pointer effect after each step.
 */
@Composable
private fun ScrollableMinutes(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    enabled: Boolean,
    layoutScale: Float
) {
    val latestMinutes by rememberUpdatedState(minutes)
    val latestOnMinutesChange by rememberUpdatedState(onMinutesChange)
    val density = LocalDensity.current
    val dragThreshold = with(density) { 18.dp.toPx() }

    Column(
        modifier = Modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                latestOnMinutesChange(
                    (latestMinutes + Trip.PLANNED_MINUTES_STEP)
                        .coerceAtMost(Trip.MAX_PLANNED_MINUTES)
                )
            },
            enabled = enabled && minutes < Trip.MAX_PLANNED_MINUTES,
            modifier = Modifier.size(32.dp * layoutScale)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_drop_up),
                contentDescription = "Increase planned time by five minutes",
                tint = TransitWhite,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = minutes.toString(),
            color = TransitWhite,
            fontSize = 28.sp * layoutScale,
            lineHeight = 30.sp * layoutScale,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
                .pointerInput(enabled, dragThreshold) {
                    if (enabled) {
                        var dragDistance = 0f
                        var workingMinutes = latestMinutes

                        detectVerticalDragGestures(
                            onDragStart = {
                                dragDistance = 0f
                                workingMinutes = latestMinutes
                            },
                            onVerticalDrag = { change, amount ->
                                change.consume()
                                dragDistance += amount

                                while (abs(dragDistance) >= dragThreshold) {
                                    val direction =
                                        if (dragDistance < 0f) 1 else -1

                                    workingMinutes = (
                                            workingMinutes +
                                                    direction *
                                                    Trip.PLANNED_MINUTES_STEP
                                            ).coerceIn(
                                        Trip.MIN_PLANNED_MINUTES,
                                        Trip.MAX_PLANNED_MINUTES
                                    )

                                    latestOnMinutesChange(workingMinutes)
                                    dragDistance += direction * dragThreshold
                                }
                            }
                        )
                    }
                }
                .semantics {
                    contentDescription =
                        "Planned time: $minutes minutes. " +
                                "Drag up to increase or down to decrease."
                }
        )

        IconButton(
            onClick = {
                latestOnMinutesChange(
                    (latestMinutes - Trip.PLANNED_MINUTES_STEP)
                        .coerceAtLeast(Trip.MIN_PLANNED_MINUTES)
                )
            },
            enabled = enabled && minutes > Trip.MIN_PLANNED_MINUTES,
            modifier = Modifier.size(32.dp * layoutScale)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_drop_down),
                contentDescription = "Decrease planned time by five minutes",
                tint = TransitWhite,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
