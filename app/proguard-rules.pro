# ══════════════════════════════════════════════════════════════════
# Codeingo Proguard / R8 Optimization Rules
# ══════════════════════════════════════════════════════════════════

# Keep line numbers and file names for debuggable crash stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature

# ── Activity & Activity Aliases ────────────────────────────────────
# Ensure launcher activity and all dynamic icon aliases are preserved
-keep class com.simats.codeingo.MainActivity { *; }
-keepnames class com.simats.codeingo.MainActivity**
-keep public class com.simats.codeingo.CodeingoApp { *; }

# ── Security & Cryptography ─────────────────────────────────────────
# Preserve PBKDF2 hasher, Android KeyStore AES-GCM helper, and security models
-keep class com.simats.codeingo.security.** { *; }

# ── DataStore & Preferences ────────────────────────────────────────
-keepclassmembers class androidx.datastore.preferences.** { *; }
-keep class androidx.datastore.preferences.core.Preferences* { *; }
-keep class com.simats.codeingo.data.local.** { *; }

# ── Kotlinx Serialization & Data Models ────────────────────────────
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.InternalSerializationApi <methods>;
}
-keep class * implements kotlinx.serialization.KSerializer {
    <init>(...);
}
-keep class com.simats.codeingo.data.model.** { *; }

# ── Coil Image & GIF Loading ────────────────────────────────────────
-keep class coil.** { *; }
-keep class coil.decode.** { *; }
-keepclassmembers class * implements coil.decode.Decoder { *; }
-keepclassmembers class * implements coil.decode.Decoder$Factory { *; }
-dontwarn coil.**

# ── Kotlin Coroutines ───────────────────────────────────────────────
-dontwarn kotlinx.coroutines.**
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
