import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RadialCircularProgressBar(
    progress: Float,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color,
) {
    // Convert progress to degrees
    val degrees = (360 * progress)

    Box(
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.toPx() / 2
            val centerY = size.toPx() / 2
            val radius = size.toPx() / 2

            // Draw inactive progress (gray)
            drawCircle(
                color = inactiveColor,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 16f)
            )

            // Draw active progress (blue)
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = degrees,
                useCenter = false,
                topLeft = Offset(16f, 16f),
                size = Size(size.toPx() - 32f, size.toPx() - 32f),
                style = Stroke(width = 16f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewRadialCircularProgressBar() {
    RadialCircularProgressBar(
        progress = 0.45f, // Change this value to 9/20 (0.45)
        size = 120.dp,
        activeColor = Color.Blue,
        inactiveColor = Color.Gray
    )
}
