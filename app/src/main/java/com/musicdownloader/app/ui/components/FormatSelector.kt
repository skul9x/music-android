package com.musicdownloader.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.musicdownloader.app.data.models.DownloadFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatSelector(
    selectedFormat: DownloadFormat,
    onFormatSelected: (DownloadFormat) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("format_selector"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val formats = listOf<Triple<DownloadFormat, String, ImageVector>>(
            Triple(DownloadFormat.M4A_AUDIO, "M4A Audio", Icons.Default.Audiotrack),
            Triple(DownloadFormat.VIDEO_1080P, "1080p HD", Icons.Default.Hd),
            Triple(DownloadFormat.VIDEO_720P, "720p Video", Icons.Default.VideoLibrary)
        )

        formats.forEach { (format, label, icon) ->
            val isSelected = selectedFormat == format
            FilterChip(
                selected = isSelected,
                onClick = { if (enabled) onFormatSelected(format) },
                label = { Text(label) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(18.dp)
                    )
                },
                enabled = enabled,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE94057),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.05f),
                    labelColor = Color.White.copy(alpha = 0.7f),
                    iconColor = Color.White.copy(alpha = 0.7f),
                    disabledContainerColor = Color.White.copy(alpha = 0.02f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = enabled,
                    selected = isSelected,
                    borderColor = Color.White.copy(alpha = 0.1f),
                    selectedBorderColor = Color(0xFFE94057)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
