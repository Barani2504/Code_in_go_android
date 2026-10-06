package com.simats.codeingo

import android.app.Application
import com.simats.codeingo.domain.PhoenixEmotionManager

class CodeingoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PhoenixEmotionManager.instance.initialize(this)
    }
}
