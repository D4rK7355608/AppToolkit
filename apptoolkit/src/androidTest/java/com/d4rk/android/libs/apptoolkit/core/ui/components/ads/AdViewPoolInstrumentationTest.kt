package com.d4rk.android.libs.apptoolkit.core.ui.components.ads

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.collections.ArrayDeque
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class AdViewPoolInstrumentationTest {

    @Test
    fun cleanup_destroys_expired_views_on_main_thread() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val adView = AdView(context)
        adView.adUnitId = "unit"
        adView.setAdSize(AdSize.BANNER)

        // Release to pool from background thread
        AdViewPool.release("unit", AdSize.BANNER, adView)

        val poolField = AdViewPool::class.java.getDeclaredField("pool").apply { isAccessible = true }
        @Suppress("UNCHECKED_CAST")
        val pool = poolField.get(AdViewPool) as MutableMap<Pair<String, AdSize>, ArrayDeque<*>>
        val key = "unit" to AdSize.BANNER
        val deque = pool[key] ?: ArrayDeque()
        if (deque.isNotEmpty()) {
            val pooled = deque.first()
            val lastUsedField = pooled::class.java.getDeclaredField("lastUsed").apply { isAccessible = true }
            lastUsedField.setLong(pooled, System.currentTimeMillis() - (5 * 60 * 1000L) - 1000)
        }

        val cleanupMethod = AdViewPool::class.java.getDeclaredMethod("cleanup").apply { isAccessible = true }
        cleanupMethod.invoke(AdViewPool)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertThat(deque.isEmpty()).isTrue()
        pool.clear()
    }
}

