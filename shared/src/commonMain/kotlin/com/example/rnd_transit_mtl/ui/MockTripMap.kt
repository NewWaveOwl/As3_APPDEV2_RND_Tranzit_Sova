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
import androidx.compose.ui.platform.LocalDensity
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
 * Uses centered Fit scaling inside an inset area without cropping.
 * The image, endpoints, route, and person use the same map rectangle.
 *
 * Normalized coordinates are not pixels or geographic coordinates.
 * This component does not generate endpoints, own a timer,
 * animate independently, or mutate trip state.
 */
@Composable
fun MockTripMap(
    start: TripPoint,
    destination: TripPoint,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val mapPainter = painterResource(Res.drawable.map_sample)
    val boundedProgress = boundedTripProgress(progress)
    val percentage = tripProgressPercentage(boundedProgress)

    /*
     * The bundled map is 1510 × 746. These dimensions also provide
     * its aspect ratio while the web resource painter is loading.
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
        /*
         * Dp-to-pixel conversions require an explicit Density receiver.
         */
        val containerSize = with(density) {
            Size(
                width = maxWidth.toPx(),
                height = maxHeight.toPx()
            )
        }

        /*
         * Scale markers down for unusually small maps.
         * Clearance keeps markers visible at normalized coordinates 0 and 1.
         */
        val markerSize = minOf(
            48.dp,
            maxWidth / 4f,
            maxHeight / 4f
        )

        val markerPixels = with(density) {
            markerSize.toPx()
        }
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
                 * Draw the image into the same rectangle used
                 * to transform all normalized route coordinates.
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

                // Start: circular yellow boundary.
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

                // Destination: distinct green square.
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
             * Its center is anchored to the stored start position.
             */
            val startBadgeSize = markerSize * 0.78f
            val startBadgePixels = with(density) {
                startBadgeSize.toPx()
            }

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
             * Float translation preserves subpixel movement.
             * The person's center stays anchored to the route.
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
 * Computes centered Fit scaling with equal marker clearance.
 * Letterboxing remains visible rather than cropping the image.
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