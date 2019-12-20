package org.mozilla.reference.browser.ui

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mozilla.reference.browser.helpers.TestAssetHelper.waitingTime

class ExternalIntentsTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)

    @Test
    fun openExternalLink() {
        device.waitForIdle()
        instrumentation.uiAutomation.executeShellCommand("am start -a ${Intent.ACTION_VIEW} -d https://cliqz.com")
        device.waitForIdle()
        val daisy = device.wait(Until.findObject(By.textContains("Daisy")), waitingTime)
        daisy?.apply {
            click()
            val justOnce = device.findObject(By.text("Just once"))
            justOnce?.click()
            justOnce?.recycle()
            recycle()
        }
        device.waitForIdle()
        val url = device.wait(Until.findObject(By.textContains("https://cliqz.com")), waitingTime)
        try {
            assertNotNull(url)
        } finally {
            url?.recycle()
        }
    }
}