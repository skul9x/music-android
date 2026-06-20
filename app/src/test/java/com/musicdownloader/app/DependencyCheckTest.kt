package com.musicdownloader.app

import org.junit.Assert.assertNotNull
import org.junit.Test

class DependencyCheckTest {
    @Test
    fun `youtubedl classes are on classpath`() {
        val clazz = Class.forName("com.yausername.youtubedl_android.YoutubeDL")
        assertNotNull("YoutubeDL class should be resolvable", clazz)
    }

    @Test
    fun `jackson classes are on classpath`() {
        val clazz = Class.forName("com.fasterxml.jackson.databind.ObjectMapper")
        assertNotNull("ObjectMapper should be resolvable", clazz)
    }

    @Test
    fun `compose material3 classes are on classpath`() {
        val clazz = Class.forName("androidx.compose.material3.MaterialTheme")
        assertNotNull("Material3 MaterialTheme class should be resolvable", clazz)
    }
}
