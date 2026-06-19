package com.musicdownloader.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.musicdownloader.app.util.ClipboardHelper

@Composable
fun UrlInputSection(
    url: String,
    onUrlChange: (String) -> Unit,
    onFetchClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("url_input"),
            enabled = enabled,
            label = { Text("Enter Video/Music URL", color = Color.White.copy(alpha = 0.6f)) },
            placeholder = { Text("https://example.com/video", color = Color.White.copy(alpha = 0.3f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFE94057),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                disabledBorderColor = Color.White.copy(alpha = 0.1f),
                cursorColor = Color(0xFFE94057)
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        val pasted = ClipboardHelper.getClipboardText(context)
                        if (pasted != null) {
                            onUrlChange(pasted)
                        }
                    },
                    modifier = Modifier.testTag("paste_button"),
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste from Clipboard",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            },
            singleLine = true
        )

        GlassButton(
            text = "Fetch Info",
            onClick = onFetchClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fetch_info_button"),
            enabled = enabled && url.isNotBlank()
        )
    }
}
