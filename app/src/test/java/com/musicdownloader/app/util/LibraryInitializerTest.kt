package com.musicdownloader.app.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryInitializerTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        LibraryInitializer.reset()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is not initialized and has no error`() {
        assertFalse(LibraryInitializer.isInitialized.value)
        assertNull(LibraryInitializer.initError.value)
    }

    @Test
    fun `initialize handles errors and updates initError`() = runTest {
        val mockContext = mock(Context::class.java)
        
        LibraryInitializer.initialize(mockContext)
        
        // Wait for initError to be set on standard background Dispatchers.IO coroutine
        val error = LibraryInitializer.initError.first { it != null }
        
        assertFalse(LibraryInitializer.isInitialized.value)
        assertNotNull(error)
    }
}
