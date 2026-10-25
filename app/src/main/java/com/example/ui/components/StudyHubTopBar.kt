package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandDeepTeal
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.StatusDanger

@Composable
fun StudyHubLogoMark(modifier: Modifier = Modifier, size: Dp = 34.dp) {
    val tealPrimary = MaterialTheme.colorScheme.primary
    val tealDeep = BrandDeepTeal
    val cyanAccent = Color(0xFF22D3EE)

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Left Book wing with gradient
        val leftPage = Path().apply {
            moveTo(w * 0.12f, h * 0.68f)
            lineTo(w * 0.48f, h * 0.86f)
            lineTo(w * 0.48f, h * 0.38f)
            lineTo(w * 0.12f, h * 0.28f)
            close()
        }
        drawPath(
            path = leftPage,
            brush = Brush.verticalGradient(
                colors = listOf(tealPrimary, tealDeep),
                startY = h * 0.28f,
                endY = h * 0.86f
            )
        )

        // Right Book wing
        val rightPage = Path().apply {
            moveTo(w * 0.88f, h * 0.68f)
            lineTo(w * 0.52f, h * 0.86f)
            lineTo(w * 0.52f, h * 0.38f)
            lineTo(w * 0.88f, h * 0.28f)
            close()
        }
        drawPath(
            path = rightPage,
            brush = Brush.verticalGradient(
                colors = listOf(tealDeep, Color(0xFF065F69)),
                startY = h * 0.28f,
                endY = h * 0.86f
            )
        )

        // Dynamic Achievement Arrow (rising upward from center)
        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.74f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.38f)
            lineTo(w * 0.58f, h * 0.72f)
            lineTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.42f, h * 0.38f)
            lineTo(w * 0.26f, h * 0.38f)
            close()
        }
        drawPath(
            path = arrow,
            brush = Brush.verticalGradient(
                colors = listOf(cyanAccent, tealPrimary),
                startY = h * 0.08f,
                endY = h * 0.72f
            )
        )
    }
}

@Composable
fun StudyHubTopBar(
    modifier: Modifier = Modifier,
    unreadNotificationCount: Int = 0,
    onNotificationClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(58.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudyHubLogoMark(size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "StudyHub",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Header Action Group: Notification Bell + Settings Gear
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notification Bell with Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onNotificationClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )

                    if (unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 7.dp, end = 7.dp)
                                .size(8.5.dp)
                                .background(StatusDanger, CircleShape)
                                .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                        )
                    }
                }

                // Settings Gear Icon
                if (onSettingsClick != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onSettingsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudyHubDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(58.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            actions()
        }
    }
}
