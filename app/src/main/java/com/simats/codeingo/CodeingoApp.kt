package com.simats.codeingo

import android.app.Application
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager

class CodeingoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PhoenixEmotionManager.instance.initialize(this)
        ThemeManager.instance.initialize(this)
        LocalizationManager.instance.initialize(this)
        GameManager.instance.initialize(this)
    }
}
