package com.d4rk.android.libs.apptoolkit.app.advanced.utils

import android.content.Context
import android.widget.Toast
import com.d4rk.android.libs.apptoolkit.R
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test
import kotlin.io.path.createTempDirectory
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class TestCleanHelper {

    @Test
    fun `clearApplicationCache deletes cache directories`() {
        val dir1 = createTempDirectory().toFile()
        val dir2 = createTempDirectory().toFile()
        val dir3 = createTempDirectory().toFile()

        val context = mockk<Context>()
        every { context.cacheDir } returns dir1
        every { context.codeCacheDir } returns dir2
        every { context.filesDir } returns dir3
        every { context.getString(R.string.cache_cleared_success) } returns "success"

        mockkStatic(Toast::class)
        val toast = mockk<Toast>(relaxed = true)
        every { Toast.makeText(context, "success", Toast.LENGTH_SHORT) } returns toast

        CleanHelper.clearApplicationCache(context)

        assertFalse(dir1.exists())
        assertFalse(dir2.exists())
        assertFalse(dir3.exists())
        verify { Toast.makeText(context, "success", Toast.LENGTH_SHORT) }
    }

    @Test
    fun `clearApplicationCache shows error toast when deletion fails`() {
        val dir1 = createTempDirectory().toFile()
        val failing = mockk<java.io.File>()
        every { failing.deleteRecursively() } returns false
        every { failing.exists() } returns true
        val dir3 = createTempDirectory().toFile()

        val context = mockk<Context>()
        every { context.cacheDir } returns dir1
        every { context.codeCacheDir } returns failing
        every { context.filesDir } returns dir3
        every { context.getString(R.string.cache_cleared_error) } returns "error"

        mockkStatic(Toast::class)
        val toast = mockk<Toast>(relaxed = true)
        every { Toast.makeText(context, "error", Toast.LENGTH_SHORT) } returns toast

        CleanHelper.clearApplicationCache(context)

        assertFalse(dir1.exists())
        assertFalse(dir3.exists())
        verify { failing.deleteRecursively() }
        verify { Toast.makeText(context, "error", Toast.LENGTH_SHORT) }
    }

    @Test
    fun `clearApplicationCache throws when directory inaccessible`() {
        val context = mockk<Context>()
        every { context.cacheDir } throws SecurityException("denied")

        assertFailsWith<SecurityException> {
            CleanHelper.clearApplicationCache(context)
        }
    }
}
