package com.simats.codeingo

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.simats.codeingo.domain.GameManager
import com.simats.codeingo.domain.LocalizationManager
import com.simats.codeingo.domain.PhoenixEmotionManager
import com.simats.codeingo.domain.ThemeManager

class CodeingoApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        PhoenixEmotionManager.instance.initialize(this)
        ThemeManager.instance.initialize(this)
        LocalizationManager.instance.initialize(this)
        GameManager.instance.initialize(this)
        com.simats.codeingo.domain.AuthService.instance.initialize(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}

