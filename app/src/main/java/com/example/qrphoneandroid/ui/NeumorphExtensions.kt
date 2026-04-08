package com.example.qrphoneandroid.ui

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.NeumorphDark
import com.example.qrphoneandroid.ui.theme.NeumorphLight
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary

/**
 * Raised / extruded neumorphic surface.
 * Light source is top-left: light highlight top-left, dark shadow bottom-right.
 */
internal fun Modifier.neumorphRaised(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 8.dp,
): Modifier = this.drawBehind {
    val r      = cornerRadius.toPx()
    val blur   = elevation.toPx() * 2f
    val offset = elevation.toPx() * 0.6f

    // Dark shadow — bottom-right
    drawIntoCanvas { canvas ->
        val p = Paint()
        p.asFrameworkPaint().apply {
            isAntiAlias = true
            color       = NeumorphDark.toArgb()
            maskFilter  = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(offset, offset, size.width + offset, size.height + offset, r, r, p)
    }
    // Light highlight — top-left
    drawIntoCanvas { canvas ->
        val p = Paint()
        p.asFrameworkPaint().apply {
            isAntiAlias = true
            color       = NeumorphLight.toArgb()
            maskFilter  = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(-offset, -offset, size.width - offset, size.height - offset, r, r, p)
    }
    // Surface fill — covers shadow centres, leaves only outer blur visible
    drawRoundRect(color = NeumorphBase, cornerRadius = CornerRadius(r))
}

/**
 * Pressed / inset neumorphic surface.
 * Shadows swap sides to simulate the element being pushed into the surface.
 */
internal fun Modifier.neumorphPressed(
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
): Modifier = this.drawBehind {
    val r      = cornerRadius.toPx()
    val blur   = elevation.toPx() * 1.8f
    val offset = elevation.toPx() * 0.5f

    // Dark shadow — top-left (inverted from raised)
    drawIntoCanvas { canvas ->
        val p = Paint()
        p.asFrameworkPaint().apply {
            isAntiAlias = true
            color       = NeumorphDark.toArgb()
            maskFilter  = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(-offset, -offset, size.width - offset, size.height - offset, r, r, p)
    }
    // Light highlight — bottom-right (inverted from raised)
    drawIntoCanvas { canvas ->
        val p = Paint()
        p.asFrameworkPaint().apply {
            isAntiAlias = true
            color       = NeumorphLight.toArgb()
            maskFilter  = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(offset, offset, size.width + offset, size.height + offset, r, r, p)
    }
    // Surface fill
    drawRoundRect(color = NeumorphBase, cornerRadius = CornerRadius(r))
}

/** Primary CTA button: raised at rest, pressed/inset on press. */
@Composable
internal fun NeumorphPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .padding(4.dp)
            .then(
                if (isPressed) Modifier.neumorphPressed(cornerRadius = 14.dp, elevation = 5.dp)
                else           Modifier.neumorphRaised(cornerRadius = 14.dp, elevation = 6.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            ),
    ) {
        Text(
            text       = text,
            color      = QRPrimary,
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp,
        )
    }
}

/** Secondary / cancel button: always appears inset to show lower hierarchy. */
@Composable
internal fun NeumorphSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .padding(4.dp)
            .neumorphPressed(
                cornerRadius = 14.dp,
                elevation    = if (isPressed) 7.dp else 4.dp,
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            ),
    ) {
        Text(
            text       = text,
            color      = QROnSurfaceVariant,
            fontWeight = FontWeight.Medium,
            fontSize   = 15.sp,
        )
    }
}

/** Square icon tile used in action grids (e.g. QRDisplayScreen). */
@Composable
internal fun NeumorphIconTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = QRPrimary,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(6.dp)
            .then(
                if (isPressed) Modifier.neumorphPressed(cornerRadius = 18.dp, elevation = 5.dp)
                else           Modifier.neumorphRaised(cornerRadius = 18.dp, elevation = 8.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = tint,
                modifier           = Modifier.size(28.dp),
            )
            Text(
                text       = label,
                color      = tint,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
