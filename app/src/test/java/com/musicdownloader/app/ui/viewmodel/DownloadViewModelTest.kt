package com.musicdownloader.app.ui.viewmodel

import com.musicdownloader.app.data.models.*
import com.musicdownloader.app.data.repository.FakeDownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadViewModelTest {

    private lateinit var fakeRepo: FakeDownloadRepository
    private lateinit var viewModel: DownloadViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeDownloadRepository()
        viewModel = DownloadViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(DownloadUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `fetchInfo transitions to FetchingInfo then InfoReady`() = runTest {
        fakeRepo.videoInfoToReturn = VideoInfo(
            title = "Test Video",
            thumbnailUrl = "https://example.com/thumb.jpg",
            duration = 240,
            uploader = "TestUser",
            url = "https://youtube.com/watch?v=test123"
        )

        viewModel.fetchInfo("https://youtube.com/watch?v=test123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be InfoReady", state is DownloadUiState.InfoReady)
        assertEquals("Test Video", (state as DownloadUiState.InfoReady).videoInfo.title)
    }

    @Test
    fun `fetchInfo with invalid URL transitions to Error`() = runTest {
        viewModel.fetchInfo("not-a-valid-url")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is DownloadUiState.Error)
    }

    @Test
    fun `startDownload transitions to Success on completion`() = runTest {
        fakeRepo.downloadResultPath = "/storage/downloads/test.m4a"

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.M4A_AUDIO
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is DownloadUiState.Success)
        assertEquals("/storage/downloads/test.m4a", (state as DownloadUiState.Success).filePath)
    }

    @Test
    fun `startDownload with repo error transitions to Error`() = runTest {
        fakeRepo.shouldThrowOnDownload = true

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.M4A_AUDIO
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is DownloadUiState.Error)
    }

    @Test
    fun `cancelDownload transitions to Cancelled`() = runTest {
        fakeRepo.simulateSlowDownload = true

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.VIDEO_1080P
        )
        // Cancel before completion
        viewModel.cancelDownload()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Cancelled", state is DownloadUiState.Cancelled)
    }

    @Test
    fun `resetState returns to Idle`() = runTest {
        fakeRepo.downloadResultPath = "/storage/downloads/test.m4a"
        viewModel.startDownload("https://youtube.com/watch?v=t", "/d", DownloadFormat.M4A_AUDIO)
        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(DownloadUiState.Idle, viewModel.uiState.value)
    }
}
