# ─────────────────────────────────────────────────────────────────────────────
# QRPhoneAndroid — ProGuard / R8 rules
# ─────────────────────────────────────────────────────────────────────────────

# ── Crash-stack readability ───────────────────────────────────────────────────
# Keeps line numbers in stack traces so crash reports are still useful.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Kotlin ────────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Lazy { *; }

# ── Jetpack Compose ───────────────────────────────────────────────────────────
# Compose relies on reflection-free code generation; R8 handles it well,
# but we keep the @Composable annotation so tooling stays happy.
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ── AndroidX Security / EncryptedSharedPreferences ───────────────────────────
# The crypto classes use reflection internally — keep them in full.
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ── ML Kit Barcode Scanning ───────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_barcode.** { *; }
-dontwarn com.google.mlkit.**

# ── ZXing QR code generation ─────────────────────────────────────────────────
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ── CameraX ───────────────────────────────────────────────────────────────────
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ── Jetpack Navigation ────────────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ── ViewModel & LiveData ─────────────────────────────────────────────────────
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ── App data model (serialised into EncryptedSharedPreferences via Gson) ──────
# If you add Gson / Moshi in the future, keep the UserData fields.
-keep class com.example.qrphoneandroid.model.** { *; }

# ── Suppress noisy warnings from transitive dependencies ──────────────────────
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
