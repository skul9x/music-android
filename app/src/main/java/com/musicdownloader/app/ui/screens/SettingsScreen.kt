package com.musicdownloader.app.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.ui.components.GlassCard
import com.musicdownloader.app.ui.viewmodel.SettingsViewModel
import com.musicdownloader.app.ui.viewmodel.UpdateUiState
import com.musicdownloader.app.util.StorageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    BackHandler(onBack = onNavigateBack)

    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsState()
    val updateUiState by viewModel.updateState.collectAsState()

    var showFormatDropdown by remember { mutableStateOf(false) }

    val dirLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val physicalPath = StorageHelper.getPhysicalPathFromUri(context, it)
            if (physicalPath != null) {
                viewModel.updateDefaultSavePath(physicalPath)
                Toast.makeText(context, "Save path updated", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateDefaultSavePath(it.toString())
                Toast.makeText(context, "Using URI save path", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(updateUiState) {
        when (val state = updateUiState) {
            is UpdateUiState.AlreadyUpToDate -> {
                Toast.makeText(context, "yt-dlp is already up to date", Toast.LENGTH_SHORT).show()
                viewModel.resetUpdateState()
            }
            is UpdateUiState.Updated -> {
                Toast.makeText(context, "Updated to version: ${state.version}", Toast.LENGTH_LONG).show()
                viewModel.resetUpdateState()
            }
            is UpdateUiState.Error -> {
                Toast.makeText(context, "Update failed: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F0F1A),
                    Color(0xFF1E1E2F)
                )
            )
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Preferences
            Text(
                text = "Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.7f)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Save Location Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Save Folder",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = settings.defaultSavePath,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = { dirLauncher.launch(null) }) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Select Folder",
                                tint = Color(0xFFE94057)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Default Format Selector
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .testTag("default_format_selector"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Default Format",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = settings.defaultFormat.name.replace("_", " "),
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                        Box {
                            Button(
                                onClick = { showFormatDropdown = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Text("Choose", color = Color.White)
                            }
                            DropdownMenu(
                                expanded = showFormatDropdown,
                                onDismissRequest = { showFormatDropdown = false }
                            ) {
                                DownloadFormat.values().forEach { format ->
                                    DropdownMenuItem(
                                        text = { Text(format.name.replace("_", " ")) },
                                        onClick = {
                                            viewModel.updateDefaultFormat(format)
                                            showFormatDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Auto-paste toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clipboard Auto-Paste",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Detect compatible link on launch and prompt to download",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = settings.autoPasteEnabled,
                            onCheckedChange = { viewModel.updateAutoPasteEnabled(it) },
                            modifier = Modifier.testTag("autopaste_toggle"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFE94057),
                                checkedTrackColor = Color(0xFFE94057).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Section: System & Engine
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "System & Engine",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.7f)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "yt-dlp Version",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = viewModel.getEngineVersion(context),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val isUpdating = updateUiState is UpdateUiState.Updating
                    Button(
                        onClick = { viewModel.triggerEngineUpdate(context) },
                        enabled = !isUpdating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("update_engine_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A2387)
                        )
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                size = 20.dp,
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Update yt-dlp Engine", color = Color.White)
                        }
                    }
                }
            }

            // Version info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Music Downloader v1.0",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    size: androidx.compose.ui.unit.Dp,
    color: Color,
    strokeWidth: androidx.compose.ui.unit.Dp
) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}
