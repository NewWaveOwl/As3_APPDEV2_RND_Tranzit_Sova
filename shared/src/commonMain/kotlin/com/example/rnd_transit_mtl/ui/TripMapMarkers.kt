package com.example.rnd_transit_mtl.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.rnd_transit_mtl.ui.theme.TransitMain
import com.example.rnd_transit_mtl.ui.theme.TransitOrange

/** Triangles and circle share the route's exact screen-space anchors. */
internal fun DrawScope.drawTripEndpoint(
    center: Offset,
    radius: Float,
    color: Color
) {
    drawPath(tripTriangle(center, radius, pointsDown = true), TransitMain)
    drawPath(tripTriangle(center, radius * 0.62f, pointsDown = true), color)
}

internal fun DrawScope.drawCurrentTripCircle(
    center: Offset,
    radius: Float
) {
    drawCircle(TransitOrange, radius, center)
    drawCircle(TransitMain, radius * 0.86f, center)
    drawPath(
        tripTriangle(center, radius * 0.58f, pointsDown = false),
        TransitOrange
    )
}

private fun tripTriangle(
    center: Offset,
    radius: Float,
    pointsDown: Boolean
): Path {
    val direction = if (pointsDown) 1f else -1f
    return Path().apply {
        moveTo(center.x, center.y + radius * direction)
        lineTo(center.x - radius, center.y - radius * direction)
        lineTo(center.x + radius, center.y - radius * direction)
        close()
    }
}
