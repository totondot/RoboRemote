package com.example.roboremote.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    size: Float = 200f,
    dotSize: Float = 50f,
    onMoved: (x: Float, y: Float) -> Unit
) {
    // Current position of the knob (center is 0,0)
    var knobPosition by remember { mutableStateOf(Offset.Zero) }
    val radius = size / 2

    Box(modifier = modifier.size(size.dp)) {
        Canvas(modifier = Modifier
            .matchParentSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Snap back to center
                        knobPosition = Offset.Zero
                        onMoved(0f, 0f)
                    },
                    onDrag = { _, dragAmount ->
                        val newPos = knobPosition + dragAmount
                        // Calculate distance from center
                        val distance = sqrt(newPos.x * newPos.x + newPos.y * newPos.y)

                        // Clamp the knob inside the circle
                        knobPosition = if (distance > radius) {
                            val angle = atan2(newPos.y, newPos.x)
                            Offset(cos(angle) * radius, sin(angle) * radius)
                        } else {
                            newPos
                        }

                        // Normalize output to -1.0 to 1.0 range
                        // Invert Y because Android Canvas Y goes down
                        val normalizedX = knobPosition.x / radius
                        val normalizedY = -(knobPosition.y / radius)
                        onMoved(normalizedX, normalizedY)
                    }
                )
            }
        ) {
            // Draw the Base
            drawCircle(color = Color.LightGray.copy(alpha = 0.5f), radius = radius)

            // Draw the Knob (shifted to center of canvas)
            val centerOffset = Offset(size.dp.toPx() / 2, size.dp.toPx() / 2)
            drawCircle(
                color = Color.Blue,
                radius = dotSize,
                center = center + knobPosition
            )
        }
    }
}