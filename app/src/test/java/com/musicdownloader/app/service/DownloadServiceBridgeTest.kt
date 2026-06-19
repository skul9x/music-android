package com.musicdownloader.app.service

import com.musicdownloader.app.data.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadServiceBridgeTest {

    @Before
    fun reset() {
        DownloadServiceBridge.reset()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(DownloadUiState.Idle, DownloadServiceBridge.state.value)
    }

    @Test
    fun `updateState changes the state flow`() = runTest {
        DownloadServiceBridge.updateState(
            DownloadUiState.Downloading(
                DownloadProgress(50f, 10, "2.0MiB/s", "[download] 50%")
            )
        )
        val state = DownloadServiceBridge.state.value
        assertTrue(state is DownloadUiState.Downloading)
        assertEquals(50f, (state as DownloadUiState.Downloading).progress.percent, 0.01f)
    }

    @Test
    fun `reset returns state to Idle`() = runTest {
        DownloadServiceBridge.updateState(DownloadUiState.Success("/path"))
        DownloadServiceBridge.reset()
        assertEquals(DownloadUiState.Idle, DownloadServiceBridge.state.value)
    }
}
