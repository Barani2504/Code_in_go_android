package com.simats.codeingo

import com.simats.codeingo.data.network.api.AshCryptApiService
import com.simats.codeingo.data.network.api.CodeingoApiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 🧪 Phase 6 Unit Tests: Production Readiness, Security & Performance
 */
class Phase6ProductionReadinessTest {

    @Test
    fun testBrandIdentity_ashCryptApiServiceTypealias() {
        val clazzA = AshCryptApiService::class.java
        val clazzB = CodeingoApiService::class.java
        assertEquals("AshCryptApiService typealias must resolve identically to CodeingoApiService", clazzB, clazzA)
    }

    @Test
    fun testDocumentation_perfAndStoreListingExist() {
        val perfFile = File("../docs/PERF.md")
        val storeListingFile = File("../docs/STORE_LISTING.md")

        val perfExists = perfFile.exists() || File("docs/PERF.md").exists()
        val storeExists = storeListingFile.exists() || File("docs/STORE_LISTING.md").exists()

        assertTrue("docs/PERF.md must be generated and present", perfExists)
        assertTrue("docs/STORE_LISTING.md must be generated and present", storeExists)
    }

    @Test
    fun testManifest_allowBackupIsFalse() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        val manifestAlt = File("app/src/main/AndroidManifest.xml")
        val target = if (manifestFile.exists()) manifestFile else manifestAlt

        if (target.exists()) {
            val content = target.readText()
            assertTrue("android:allowBackup must be false for data security", content.contains("android:allowBackup=\"false\""))
        }
    }
}

