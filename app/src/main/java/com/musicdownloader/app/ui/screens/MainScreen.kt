package com.musicdownloader.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.musicdownloader.app.data.models.DownloadFormat
import com.musicdownloader.app.data.models.DownloadHistoryItem
import com.musicdownloader.app.data.models.DownloadUiState
import com.musicdownloader.app.data.models.VideoInfo
import com.musicdownloader.app.data.repository.HistoryRepository
import com.musicdownloader.app.data.repository.SettingsRepository
import com.musicdownloader.app.ui.components.DownloadErrorCard
import com.musicdownloader.app.ui.components.DownloadProgressSection
import com.musicdownloader.app.ui.components.DownloadSuccessCard
import com.musicdownloader.app.ui.components.FormatSelector
import com.musicdownloader.app.ui.components.GlassButton
import com.musicdownloader.app.ui.components.GlassCard
import com.musicdownloader.app.ui.components.UrlInputSection
import com.musicdownloader.app.ui.components.VideoInfoCard
import com.musicdownloader.app.ui.viewmodel.DownloadViewModel
import com.musicdownloader.app.util.ClipboardHelper
import com.musicdownloader.app.util.StorageHelper
import com.musicdownloader.app.util.UrlValidator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DownloadViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isLibraryReady by viewModel.isLibraryReady.collectAsState()
    val libraryError by viewModel.libraryError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val settingsRepository = remember { SettingsRepository(context) }
    val settings by settingsRepository.settingsFlow.collectAsState(initial = settingsRepository.getSettings())

    var url by rememberSaveable { mutableStateOf("") }
    var selectedFormat by rememberSaveable { mutableStateOf(DownloadFormat.M4A_AUDIO) }
    var savePath by rememberSaveable { mutableStateOf(StorageHelper.getSavePath(context)) }

    // Sync savePath and defaultFormat from settings repository
    LaunchedEffect(settings) {
        savePath = settings.defaultSavePath
        selectedFormat = settings.defaultFormat
    }

    // Auto-paste state
    var clipboardUrlToPrompt by remember { mutableStateOf<String?>(null) }
    var autoPasteChecked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(settings.autoPasteEnabled) {
        if (settings.autoPasteEnabled && !autoPasteChecked) {
            autoPasteChecked = true
            val text = ClipboardHelper.getClipboardText(context)
            if (text != null && UrlValidator.isSupported(text)) {
                clipboardUrlToPrompt = text.trim()
            }
        }
    }

    // History and video info caching
    val historyRepository = remember { HistoryRepository.getInstance() }
    val historyList by historyRepository.historyFlow.collectAsState()
    var lastVideoInfo by remember { mutableStateOf<VideoInfo?>(null) }

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                // Ignore
            }
            val physicalPath = StorageHelper.getPhysicalPathFromUri(context, it)
            val path = physicalPath ?: StorageHelper.getDefaultDownloadPath()
            StorageHelper.setSavePath(context, path)
            savePath = path
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startDownload(url, savePath, selectedFormat, context)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Notification permission is required for background downloads")
            }
            viewModel.startDownload(url, savePath, selectedFormat, context)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "background_gradient")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val colors = listOf(
        Color(0xFF140526),
        Color(0xFF051329),
        Color(0xFF042422)
    )
    val backgroundBrush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(1000f * animOffset, 1500f * animOffset)
    )

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is DownloadUiState.Cancelled -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Download cancelled")
                }
            }
            is DownloadUiState.InfoReady -> {
                lastVideoInfo = state.videoInfo
            }
            is DownloadUiState.Success -> {
                val info = lastVideoInfo
                val isPlaylist = info?.isPlaylist == true || com.musicdownloader.app.util.NetworkHelper.isPlaylistUrl(url)
                val defaultTitle = if (isPlaylist) "Playlist Folder" else "Downloaded File"
                historyRepository.addItem(
                    DownloadHistoryItem(
                        title = info?.title ?: defaultTitle,
                        filePath = state.filePath,
                        format = selectedFormat.name.replace("_", " "),
                        timestamp = System.currentTimeMillis(),
                        thumbnailUrl = info?.thumbnailUrl ?: "",
                        isPlaylist = isPlaylist
                    )
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Music Downloader",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFE94057).copy(alpha = 0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Clipboard Auto-Paste Banner
                AnimatedVisibility(visible = clipboardUrlToPrompt != null) {
                    clipboardUrlToPrompt?.let { promptUrl ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Link detected from clipboard:",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = promptUrl,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Button(
                                        onClick = { clipboardUrlToPrompt = null },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Text("Dismiss", color = Color.White)
                                    }
                                    Button(
                                        onClick = {
                                            url = promptUrl
                                            viewModel.fetchInfo(promptUrl)
                                            clipboardUrlToPrompt = null
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE94057)
                                        )
                                    ) {
                                        Text("Use link", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                when (val state = uiState) {
                    is DownloadUiState.Idle,
                    is DownloadUiState.FetchingInfo,
                    is DownloadUiState.InfoReady -> {
                        val isEnabled = state !is DownloadUiState.FetchingInfo

                        UrlInputSection(
                            url = url,
                            onUrlChange = { url = it },
                            onFetchClick = { viewModel.fetchInfo(url) },
                            enabled = isEnabled
                        )

                        if (state is DownloadUiState.FetchingInfo) {
                            CircularProgressIndicator(
                                color = Color(0xFFE94057),
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        if (state is DownloadUiState.InfoReady) {
                            VideoInfoCard(videoInfo = state.videoInfo)
                        }

                        FormatSelector(
                            selectedFormat = selectedFormat,
                            onFormatSelected = { selectedFormat = it },
                            enabled = isEnabled
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Save Path: $savePath",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            GlassButton(
                                text = "Change Save Folder",
                                onClick = { folderLauncher.launch(null) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isEnabled
                            )
                        }

                        if (libraryError != null) {
                            Text(
                                text = "Initialization error: $libraryError",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        } else if (!isLibraryReady) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFE94057),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Preparing download engine...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GlassButton(
                            text = "Download",
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    
                                    if (hasPermission) {
                                        viewModel.startDownload(url, savePath, selectedFormat, context)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.startDownload(url, savePath, selectedFormat, context)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_button"),
                            enabled = isEnabled && url.isNotBlank() && isLibraryReady
                        )
                    }

                    is DownloadUiState.Downloading -> {
                        DownloadProgressSection(
                            progress = state.progress,
                            onCancelClick = { viewModel.cancelDownload(context) }
                        )
                    }

                    is DownloadUiState.Success -> {
                        DownloadSuccessCard(
                            filePath = state.filePath,
                            onOpenFolderClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Folder: $savePath")
                                }
                            }
                        )

                        GlassButton(
                            text = "Download Another",
                            onClick = {
                                viewModel.resetState()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is DownloadUiState.Error -> {
                        DownloadErrorCard(
                            errorMessage = state.message,
                            onRetryClick = {
                                viewModel.resetState()
                            }
                        )
                    }

                    is DownloadUiState.Cancelled -> {
                        LaunchedEffect(Unit) {
                            viewModel.resetState()
                        }
                    }
                }

                // Collapsible History Section
                var isHistoryExpanded by rememberSaveable { mutableStateOf(false) }

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isHistoryExpanded = !isHistoryExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Download History (${historyList.size})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isHistoryExpanded) "Collapse" else "Expand",
                                color = Color(0xFFE94057),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }

                        AnimatedVisibility(visible = isHistoryExpanded) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                if (historyList.isEmpty()) {
                                    Text(
                                        text = "No history yet",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Button(
                                        onClick = { historyRepository.clearHistory() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.1f)
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Text("Clear History", color = Color.White, fontSize = 12.sp)
                                    }

                                    historyList.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (item.thumbnailUrl.isNotEmpty()) {
                                                AsyncImage(
                                                    model = item.thumbnailUrl,
                                                    contentDescription = "Thumbnail",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .background(
                                                            Color.White.copy(alpha = 0.1f),
                                                            RoundedCornerShape(8.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val icon = if (item.isPlaylist) {
                                                        Icons.AutoMirrored.Filled.PlaylistPlay
                                                    } else {
                                                        Icons.Default.MusicNote
                                                    }
                                                    val contentDesc = if (item.isPlaylist) "Playlist" else "Music"
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = contentDesc,
                                                        tint = Color.White.copy(alpha = 0.5f)
                                                    )
                                                }
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.title,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${item.format} • ${item.filePath}",
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    fontSize = 12.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                        Divider(color = Color.White.copy(alpha = 0.05f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
