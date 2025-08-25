package com.d4rk.android.libs.apptoolkit.core.ui.components.ads

import android.content.Context
import com.d4rk.android.libs.apptoolkit.core.utils.dispatchers.UnconfinedDispatcherExtension
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.assertDoesNotThrow

@ExtendWith(UnconfinedDispatcherExtension::class)
class AdViewPoolTest {

    private val defaultFactory = AdViewPool.viewFactory

    @AfterEach
    fun teardown() {
        AdViewPool.viewFactory = defaultFactory
        val field = AdViewPool::class.java.getDeclaredField("pool")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.get(AdViewPool) as MutableMap<*, *>).clear()
    }

    @Test
    fun `preload does not throw when exceeding pool size`() = runBlocking {
        val context = mockk<Context>(relaxed = true)
        val adView = mockk<AdView>(relaxed = true)
        every { adView.post(any()) } answers {
            firstArg<Runnable>().run()
            true
        }
        AdViewPool.viewFactory = { _, _, _ -> adView }
        val adRequest = mockk<AdRequest>()

        assertDoesNotThrow {
            AdViewPool.preload(context, "unit", AdSize.BANNER, adRequest, 4)
        }
        delay(50)
    }
}
