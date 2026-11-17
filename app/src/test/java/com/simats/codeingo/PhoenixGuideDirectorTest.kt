package com.simats.codeingo

import android.content.SharedPreferences
import com.simats.codeingo.domain.guide.CryptoGlossary
import com.simats.codeingo.domain.guide.PhoenixGuideDirector
import com.simats.codeingo.domain.guide.PhoenixPose
import com.simats.codeingo.ui.components.guide.CoachMarkState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoenixGuideDirectorTest {

    private fun createMockPrefs(store: MutableMap<String, Any>): SharedPreferences {
        return object : SharedPreferences {
            override fun getAll(): MutableMap<String, *> = store
            override fun getString(key: String?, defValue: String?): String? = store[key] as? String ?: defValue
            override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
                (store[key] as? Set<*>)?.map { it.toString() }?.toMutableSet() ?: defValues
            override fun getInt(key: String?, defValue: Int): Int = store[key] as? Int ?: defValue
            override fun getLong(key: String?, defValue: Long): Long = store[key] as? Long ?: defValue
            override fun getFloat(key: String?, defValue: Float): Float = store[key] as? Float ?: defValue
            override fun getBoolean(key: String?, defValue: Boolean): Boolean = store[key] as? Boolean ?: defValue
            override fun contains(key: String?): Boolean = store.containsKey(key)
            override fun edit(): SharedPreferences.Editor {
                return object : SharedPreferences.Editor {
                    override fun putString(k: String?, v: String?) = this.also { store[k!!] = v!! }
                    override fun putStringSet(k: String?, v: MutableSet<String>?) = this.also { store[k!!] = v?.toSet() ?: emptySet<String>() }
                    override fun putInt(k: String?, v: Int) = this.also { store[k!!] = v }
                    override fun putLong(k: String?, v: Long) = this.also { store[k!!] = v }
                    override fun putFloat(k: String?, v: Float) = this.also { store[k!!] = v }
                    override fun putBoolean(k: String?, v: Boolean) = this.also { store[k!!] = v }
                    override fun remove(k: String?) = this.also { store.remove(k) }
                    override fun clear() = this.also { store.clear() }
                    override fun commit(): Boolean = true
                    override fun apply() {}
                }
            }
            override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
            override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        }
    }

    @Test
    fun testHomeTour_hasExactlyFiveCoachMarks() {
        val homeTour = PhoenixGuideDirector.HOME_TOUR
        assertEquals("Home tour must have exactly 5 coach marks", 5, homeTour.steps.size)

        val targetTags = homeTour.steps.mapNotNull { it.targetTag }
        assertEquals("All 5 steps must have target tags", 5, targetTags.size)
        assertTrue("Must include path target", targetTags.contains("tour_path"))
        assertTrue("Must include continue button target", targetTags.contains("tour_continue_btn"))
        assertTrue("Must include streak target", targetTags.contains("tour_streak"))
        assertTrue("Must include hearts target", targetTags.contains("tour_hearts"))
        assertTrue("Must include bottom nav target", targetTags.contains("tour_bottom_nav"))
    }

    @Test
    fun testAllGuideSteps_adhereToWordCountLimit() {
        // Phase 2.1 & 2.3: Plain language, <= 18 words per line
        PhoenixGuideDirector.ALL_TOURS.forEach { tour ->
            tour.steps.forEach { step ->
                val wordCount = step.line.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                assertTrue(
                    "Step '${step.id}' in tour '${tour.id}' exceeds 18 words (got $wordCount words: '${step.line}')",
                    wordCount <= 18
                )
            }
        }
    }

    @Test
    fun testPerUserPersistence_isolationBetweenUsers() {
        val store = mutableMapOf<String, Any>()
        val mockPrefs = createMockPrefs(store)

        val userAlice = "user_alice_123"
        val userBob = "user_bob_456"

        // Initially neither user has seen HOME_TOUR
        assertFalse(PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userAlice))
        assertFalse(PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userBob))

        // Alice completes HOME_TOUR
        PhoenixGuideDirector.markTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userAlice, true)

        // Alice should be seen, Bob must NOT be affected
        assertTrue("Alice must have HOME_TOUR marked as seen", PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userAlice))
        assertFalse("Bob must NOT inherit Alice's seen state", PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userBob))
    }

    @Test
    fun testResetTour_clearsOnlyTargetTour() {
        val store = mutableMapOf<String, Any>()
        val mockPrefs = createMockPrefs(store)
        val userId = "user_charlie"

        PhoenixGuideDirector.markTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userId, true)
        PhoenixGuideDirector.markTourSeen(mockPrefs, PhoenixGuideDirector.VISUALIZER_TOUR.id, userId, true)

        assertTrue(PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userId))
        assertTrue(PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.VISUALIZER_TOUR.id, userId))

        // Reset only HOME_TOUR
        PhoenixGuideDirector.resetTour(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userId)

        assertFalse("HOME_TOUR must be reset", PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.HOME_TOUR.id, userId))
        assertTrue("VISUALIZER_TOUR must still be marked seen", PhoenixGuideDirector.isTourSeen(mockPrefs, PhoenixGuideDirector.VISUALIZER_TOUR.id, userId))
    }

    @Test
    fun testResetAllTours_clearsAllToursForUser() {
        val store = mutableMapOf<String, Any>()
        val mockPrefs = createMockPrefs(store)
        val userId = "user_dave"

        PhoenixGuideDirector.ALL_TOURS.forEach { tour ->
            PhoenixGuideDirector.markTourSeen(mockPrefs, tour.id, userId, true)
            assertTrue(PhoenixGuideDirector.isTourSeen(mockPrefs, tour.id, userId))
        }

        // Reset all
        PhoenixGuideDirector.resetAllTours(mockPrefs, userId)

        PhoenixGuideDirector.ALL_TOURS.forEach { tour ->
            assertFalse("Tour '${tour.id}' must be reset", PhoenixGuideDirector.isTourSeen(mockPrefs, tour.id, userId))
        }
    }

    @Test
    fun testCryptoGlossary_containsEssentialTermsAndValidDefinitions() {
        val essentialTerms = listOf(
            "cipher",
            "plaintext",
            "ciphertext",
            "key",
            "encryption",
            "decryption",
            "symmetric",
            "asymmetric",
            "hash",
            "avalanche",
            "salt",
            "nonce",
            "mac",
            "digital_signature",
            "diffie_hellman",
            "rsa",
            "mitm"
        )

        essentialTerms.forEach { termId ->
            val term = CryptoGlossary.findTerm(termId)
            assertNotNull("Glossary must define '$termId'", term)
            assertTrue("Definition must not be blank for '$termId'", term!!.shortDefinition.isNotBlank())
            assertTrue("Example must not be blank for '$termId'", term.example.isNotBlank())
            // Definition should be one sentence (contains at most one period at end)
            val sentences = term.shortDefinition.split(".").filter { it.isNotBlank() }
            assertTrue("Definition for '$termId' must be concise (1 sentence)", sentences.size <= 2)
        }

        // Test search
        val searchResults = CryptoGlossary.searchTerms("cipher")
        assertTrue("Search for 'cipher' must yield results", searchResults.isNotEmpty())
        assertTrue(searchResults.any { it.term.contains("Cipher", ignoreCase = true) })
    }

    @Test
    fun testCoachMarkState_progressionLogic() {
        val state = CoachMarkState()
        val script = PhoenixGuideDirector.HOME_TOUR

        state.startTour(script)
        assertEquals(script, state.activeScript)
        assertEquals(0, state.currentStepIndex)
        assertEquals("home_step_path", state.currentStep?.id)

        // Advance to step 1
        state.nextStep()
        assertEquals(1, state.currentStepIndex)
        assertEquals("home_step_continue", state.currentStep?.id)

        // Advance to remaining steps
        state.nextStep() // 2
        state.nextStep() // 3
        state.nextStep() // 4
        assertEquals(4, state.currentStepIndex)
        assertEquals("home_step_bottom_nav", state.currentStep?.id)

        var finishedCallbackCalled = false
        state.nextStep {
            finishedCallbackCalled = true
        }

        assertTrue("Finished callback must be invoked on final step", finishedCallbackCalled)
        assertEquals("activeScript must be null after tour completion", null, state.activeScript)
    }
}
