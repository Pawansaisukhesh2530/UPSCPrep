package com.example.upscprep.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

fun Modifier.microInteractionClickable(
    enabled: Boolean = true,
    indication: Indication? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "press-scale"
    )

    val rippleIndication = indication ?: ripple(color = GoldAmber.copy(alpha = 0.4f))

    this
        .scale(scale)
        .clip(RoundedCornerShape(16.dp))
        .semantics { role?.let { this.role = it } }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = rippleIndication,
            onClick = onClick
        )
}

