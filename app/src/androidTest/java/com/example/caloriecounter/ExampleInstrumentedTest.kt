package com.example.caloriecounter

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Получаем контекст приложения
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // Проверяем, что packageName совпадает с ожидаемым
        assertEquals("com.example.caloriecounter", appContext.packageName)
    }
}