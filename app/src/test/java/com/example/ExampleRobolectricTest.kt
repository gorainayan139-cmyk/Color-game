package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PredictColor
import com.example.data.model.PredictSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Color Predictor", appName)
    }

    @Test
    fun `test color and size prediction rules`() {
        // Number 0 has Red and Violet, and is Small
        val colors0 = PredictColor.fromNumber(0)
        assertTrue(colors0.contains(PredictColor.RED))
        assertTrue(colors0.contains(PredictColor.VIOLET))
        assertEquals(PredictSize.SMALL, PredictSize.fromNumber(0))

        // Number 5 has Green and Violet, and is Big
        val colors5 = PredictColor.fromNumber(5)
        assertTrue(colors5.contains(PredictColor.GREEN))
        assertTrue(colors5.contains(PredictColor.VIOLET))
        assertEquals(PredictSize.BIG, PredictSize.fromNumber(5))

        // Number 7 is Green, Big
        val colors7 = PredictColor.fromNumber(7)
        assertTrue(colors7.contains(PredictColor.GREEN))
        assertEquals(PredictSize.BIG, PredictSize.fromNumber(7))

        // Number 4 is Red, Small
        val colors4 = PredictColor.fromNumber(4)
        assertTrue(colors4.contains(PredictColor.RED))
        assertEquals(PredictSize.SMALL, PredictSize.fromNumber(4))
    }
}
