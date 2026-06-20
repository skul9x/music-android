package com.musicdownloader.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicdownloader.app.data.models.DownloadProgress
import com.musicdownloader.app.util.formatDuration

@Composable
fun DownloadProgressSection(
    progress: DownloadProgress,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("download_progress_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val postProcessStatus = progress.getPostProcessingStatus()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            val statusText = if (postProcessStatus != null) {
                if (progress.totalItems > 0) {
                    "Item ${progress.currentItem} of ${progress.totalItems}: $postProcessStatus"
                } else {
                    postProcessStatus
                }
            } else if (progress.totalItems > 0) {
                "Downloading item ${progress.currentItem} of ${progress.totalItems}"
            } else {
                "Downloading..."
            }
            Text(
                text = statusText,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("progress_status")
            )

            IconButton(
                onClick = onCancelClick,
                modifier = Modifier.testTag("cancel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Download",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (postProcessStatus != null || progress.percent < 0) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("progress_bar"),
                color = Color(0xFFE94057),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        } else {
            val progressFraction = (progress.percent / 100f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("progress_bar"),
                color = Color(0xFFE94057),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val percentText = if (postProcessStatus != null) {
                "--%"
            } else {
                "${progress.percent.toInt()}%"
            }
            Text(
                text = percentText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.testTag("progress_percentage")
            )

            val speedText = if (postProcessStatus != null) {
                "Processing"
            } else {
                progress.speedStr
            }
            Text(
                text = speedText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.testTag("progress_speed")
            )

            val etaStr = if (postProcessStatus != null) {
                "ETA: --:--"
            } else if (progress.etaSeconds > 0) {
                "ETA: ${formatDuration(progress.etaSeconds)}"
            } else {
                "ETA: --:--"
            }
            Text(
                text = etaStr,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.testTag("progress_eta")
            )
        }
        }
    }
}
