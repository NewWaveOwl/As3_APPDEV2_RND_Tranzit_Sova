package com.example.rnd_transit_mtl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.rnd_transit_mtl.model.TripPoint
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitOrange
import com.example.rnd_transit_mtl.ui.theme.TransitSelected
import com.example.rnd_transit_mtl.ui.theme.TransitWhite
import org.jetbrains.compose.resources.painterResource
import rnd_transit_mtl.shared.generated.resources.Res
import rnd_transit_mtl.shared.generated.resources.map_sample

/**
 * The complete bundled PNG fills a normalized drawing rectangle.
 * Both the image and every route anchor use that same rectangle.
 * Pinch/drag changes only the saved camera; endpoints and progress stay stored.
 *
 * This deliberately stretches the mock PNG to the available rectangle instead
 * of cropping away random endpoints. It does not represent geographic routing.
 */
@Composable
fun MockTripMap(
    start: TripPoint,
    destination: TripPoint,
    progress: Float,
    modifier: Modifier = Modifier,
    interactive: Boolean = false
) {
    val mapPainter = painterResource(Res.drawable.map_sample)
    val boundedProgress = boundedTripProgress(progress)
    val percentage = tripProgressPercentage(boundedProgress)
    var zoom by rememberSaveable(start, destination) { mutableStateOf(1f) }
    var panX by rememberSaveable(start, destination) { mutableStateOf(0f) }
    var panY by rememberSaveable(start, destination) { mutableStateOf(0f) }

    fun setZoom(value: Float) {
        zoom = value.coerceIn(1f, 4f)
        val limit = (zoom - 1f) / 2f
        panX = panX.coerceIn(-limit, limit)
        panY = panY.coerceIn(-limit, limit)
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().clipToBounds()
            .background(TransitMain)
            .semantics {
                contentDescription = "Mock transit map. Orange start triangle, " +
                    "green destination triangle, and a moving circle. " +
                    if (interactive) "Pinch to zoom and drag to move the picture." else ""
                stateDescription = "$percentage percent complete"
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(interactive, start, destination) {
                    if (!interactive) return@pointerInput
                    detectTransformGestures { centroid, pan, zoomChange, _ ->
                        if (size.width > 0 && size.height > 0) {
                            val oldZoom = zoom
                            val newZoom = (oldZoom * zoomChange).coerceIn(1f, 4f)
                            val ratio = newZoom / oldZoom
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val oldPan = Offset(panX * size.width, panY * size.height)
                            val newPan = (centroid - center) -
                                (centroid - center - oldPan) * ratio + pan
                            val limit = (newZoom - 1f) / 2f
                            zoom = newZoom
                            panX = (newPan.x / size.width).coerceIn(-limit, limit)
                            panY = (newPan.y / size.height).coerceIn(-limit, limit)
                        }
                    }
                }
        ) {
            val mapRect = Rect(
                left = size.width * (1f - zoom) / 2f + panX * size.width,
                top = size.height * (1f - zoom) / 2f + panY * size.height,
                right = size.width * (1f + zoom) / 2f + panX * size.width,
                bottom = size.height * (1f + zoom) / 2f + panY * size.height
            )
            translate(mapRect.left, mapRect.top) {
                with(mapPainter) { draw(size = mapRect.size) }
            }

            val startPosition = start.toMapPosition(mapRect)
            val endPosition = destination.toMapPosition(mapRect)
            val currentPosition = Offset(
                x = startPosition.x + (endPosition.x - startPosition.x) * boundedProgress,
                y = startPosition.y + (endPosition.y - startPosition.y) * boundedProgress
            )
            val markerRadius = minOf(24.dp.toPx(), size.width * 0.08f, size.height * 0.12f)
            val routeWidth = minOf(10.dp.toPx(), markerRadius * 0.5f)
            drawLine(
                TransitMain, startPosition, endPosition,
                strokeWidth = routeWidth, cap = StrokeCap.Round
            )
            if (boundedProgress > 0f) {
                drawLine(
                    TransitOrange, startPosition, currentPosition,
                    strokeWidth = routeWidth * 0.65f, cap = StrokeCap.Round
                )
            }

            drawCurrentTripCircle(currentPosition, markerRadius)
            drawTripEndpoint(startPosition, markerRadius * 0.82f, TransitOrange)
            drawTripEndpoint(endPosition, markerRadius * 0.82f, TransitSelected)
        }

        if (interactive) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                    .background(TransitMain, RoundedCornerShape(16.dp)),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                TextButton(
                    onClick = { setZoom(zoom / 1.25f) },
                    enabled = zoom > 1f,
                    modifier = Modifier.size(48.dp)
                        .semantics { contentDescription = "Zoom out" }
                ) {
                    Text("−", color = TransitWhite)
                }
                TextButton(
                    onClick = { setZoom(zoom * 1.25f) },
                    enabled = zoom < 4f,
                    modifier = Modifier.size(48.dp)
                        .semantics { contentDescription = "Zoom in" }
                ) {
                    Text("+", color = TransitWhite)
                }
                TextButton(
                    onClick = {
                        zoom = 1f
                        panX = 0f
                        panY = 0f
                    }
                ) {
                    Text("Reset", color = TransitWhite)
                }
            }
        }
    }
}

private fun TripPoint.toMapPosition(mapRect: Rect): Offset =
    Offset(mapRect.left + x * mapRect.width, mapRect.top + y * mapRect.height)
