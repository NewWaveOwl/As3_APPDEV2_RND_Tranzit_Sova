package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.TripPoint
import com.example.rnd_transit_mtl.ui.theme.TransitHighlight
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitSelected
import com.example.rnd_transit_mtl.ui.theme.TransitWhite
import org.jetbrains.compose.resources.painterResource
import rnd_transit_mtl.shared.generated.resources.Res
import rnd_transit_mtl.shared.generated.resources.map_sample
import rnd_transit_mtl.shared.generated.resources.map_user_pointer_badge
import kotlin.math.min

/**
 * Stateless presentation of one stored mock route.
 *
 * Uses Fit scaling and centered alignment inside an inset area.
 * The image is never cropped. mapRect is the exact displayed image
 * rectangle, including scaling, alignment, and marker-clearance offsets.
 *
 * Start, destination, route, and traveller all use the same transform.
 * Normalized coordinates are not pixels, GPS positions, or street routes.
 *
 * This component does not generate endpoints, animate independently,
 * own a timer, or mutate state. Its caller supplies progress.
 */
@Composable
fun MockTripMap(
    start: TripPoint,
    destination: TripPoint,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val mapPainter = painterResource(Res.drawable.map_sample)
    val boundedProgress = boundedTripProgress(progress)
    val percentage = tripProgressPercentage(boundedProgress)

    /*
     * The verified bundled map is 1510 × 746. These dimensions also
     * supply its aspect ratio while the web resource painter is loading.
     */
    val intrinsic = mapPainter.intrinsicSize
    val sourceSize = if (
        intrinsic.width.isFinite() &&
        intrinsic.height.isFinite() &&
        intrinsic.width > 0f &&
        intrinsic.height > 0f
    ) {
        intrinsic
    } else {
        Size(1510f, 746f)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.45f)
            .clip(RoundedCornerShape(24.dp))
            .background(TransitMain)
            .semantics {
                contentDescription =
                    "Bundled mock map. " +
                            "Start: ${start.label}, shown with a yellow circular badge. " +
                            "Destination: ${destination.label}, shown with a green square. " +
                            "An orange person follows the straight connecting line."
                stateDescription = "$percentage percent complete"
            }
    ) {
        val containerSize = Size(
            width = maxWidth.toPx(),
            height = maxHeight.toPx()
        )

        /*
         * Scale markers down only for unusually small constrained maps.
         * The inset exceeds the endpoint and person half-extents, so
         * coordinates at 0 or 1 still leave every marker visible.
         */
        val markerSize = minOf(
            48.dp,
            maxWidth / 4f,
            maxHeight / 4f
        )
        val markerPixels = markerSize.toPx()
        val clearancePixels = markerPixels * 0.75f

        val mapRect = fittedMapRect(
            container = containerSize,
            source = sourceSize,
            clearance = clearancePixels
        )

        val startPosition = start.toMapPosition(mapRect)
        val destinationPosition = destination.toMapPosition(mapRect)

        val currentX =
            start.x + (destination.x - start.x) * boundedProgress
        val currentY =
            start.y + (destination.y - start.y) * boundedProgress

        val personPosition = Offset(
            x = mapRect.left + currentX * mapRect.width,
            y = mapRect.top + currentY * mapRect.height
        )

        if (mapRect.width > 0f && mapRect.height > 0f) {
            Canvas(Modifier.matchParentSize()) {
                /*
                 * Drawing into mapRect is equivalent to centered
                 * ContentScale.Fit inside the inset area.
                 */
                translate(
                    left = mapRect.left,
                    top = mapRect.top
                ) {
                    with(mapPainter) {
                        draw(size = mapRect.size)
                    }
                }

                val endpointRadius = markerPixels * 0.62f
                val routeBorderWidth = markerPixels * 0.22f
                val routeWidth = markerPixels * 0.13f

                drawLine(
                    color = TransitMain,
                    start = startPosition,
                    end = destinationPosition,
                    strokeWidth = routeBorderWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = TransitHighlight,
                    start = startPosition,
                    end = destinationPosition,
                    strokeWidth = routeWidth,
                    cap = StrokeCap.Round
                )

                if (boundedProgress > 0f) {
                    drawLine(
                        color = TransitSelected,
                        start = startPosition,
                        end = personPosition,
                        strokeWidth = routeWidth,
                        cap = StrokeCap.Round
                    )
                }

                // Start: circular yellow boundary around the user badge.
                drawCircle(
                    color = TransitMain,
                    radius = endpointRadius,
                    center = startPosition
                )
                drawCircle(
                    color = TransitHighlight,
                    radius = endpointRadius,
                    center = startPosition,
                    style = Stroke(
                        width = markerPixels * 0.07f
                    )
                )

                // Destination: green square, distinct in shape and color.
                val destinationTopLeft = Offset(
                    x = destinationPosition.x - endpointRadius,
                    y = destinationPosition.y - endpointRadius
                )
                val destinationSize = Size(
                    width = endpointRadius * 2f,
                    height = endpointRadius * 2f
                )
                drawRect(
                    color = TransitSelected,
                    topLeft = destinationTopLeft,
                    size = destinationSize
                )
                drawRect(
                    color = TransitMain,
                    topLeft = destinationTopLeft,
                    size = destinationSize,
                    style = Stroke(
                        width = markerPixels * 0.06f
                    )
                )
                drawCircle(
                    color = TransitWhite,
                    radius = markerPixels * 0.16f,
                    center = destinationPosition
                )
            }

            /*
             * Preserve the supplied pointer's original colors.
             * Its center uses the same startPosition as the Canvas marker.
             */
            val startBadgeSize = markerSize * 0.78f
            val startBadgePixels = startBadgeSize.toPx()
            Icon(
                painter = painterResource(
                    Res.drawable.map_user_pointer_badge
                ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(startBadgeSize)
                    .graphicsLayer {
                        translationX =
                            startPosition.x - startBadgePixels / 2f
                        translationY =
                            startPosition.y - startBadgePixels / 2f
                    }
            )

            /*
             * Float translation retains subpixel movement.
             * The marker's center remains anchored to personPosition.
             */
            OrangePersonMarker(
                description = null,
                modifier = Modifier
                    .size(markerSize)
                    .graphicsLayer {
                        translationX =
                            personPosition.x - markerPixels / 2f
                        translationY =
                            personPosition.y - markerPixels / 2f
                    }
            )
        }
    }
}

/**
 * Computes centered Fit scaling inside a container with equal clearance.
 *
 * Letterboxing remains visible instead of cropping map content.
 */
private fun fittedMapRect(
    container: Size,
    source: Size,
    clearance: Float
): Rect {
    val availableWidth =
        (container.width - clearance * 2f).coerceAtLeast(0f)
    val availableHeight =
        (container.height - clearance * 2f).coerceAtLeast(0f)

    val scale = min(
        availableWidth / source.width,
        availableHeight / source.height
    )
    val displayedWidth = source.width * scale
    val displayedHeight = source.height * scale
    val left = (container.width - displayedWidth) / 2f
    val top = (container.height - displayedHeight) / 2f

    return Rect(
        left = left,
        top = top,
        right = left + displayedWidth,
        bottom = top + displayedHeight
    )
}

private fun TripPoint.toMapPosition(mapRect: Rect): Offset =
    Offset(
        x = mapRect.left + x * mapRect.width,
        y = mapRect.top + y * mapRect.height
    )