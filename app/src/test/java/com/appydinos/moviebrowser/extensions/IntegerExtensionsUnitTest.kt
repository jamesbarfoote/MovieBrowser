package com.appydinos.moviebrowser.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class IntegerExtensionsUnitTest {

    @Test
    fun `toHoursAndMinutes - converts to hours and minutes text correctly`() {
        //When
        val result = 159.toHoursAndMinutes()

        //Then
        assertEquals("2h 39m", result)
    }

    @Test
    fun `toHoursAndMinutes - converts correctly when only minutes`() {
        //When
        val result = 59.toHoursAndMinutes()

        //Then
        assertEquals("59m", result)
    }

    @Test
    fun `toHoursAndMinutes - converts correctly when zero minutes`() {
        //When
        val result = 0.toHoursAndMinutes()

        //Then
        assertEquals("0m", result)
    }

    @Test
    fun `toHoursAndMinutes - converts correctly when hours and zero minutes`() {
        //When
        val result = 120.toHoursAndMinutes()

        //Then
        assertEquals("2h 0m", result)
    }
}