# Keep youtubedl-android and ffmpeg-android classes to prevent R8/ProGuard obfuscation/stripping of JNI classes
-keep class com.yausername.youtubedl_android.** { *; }
-keep class com.yausername.ffmpeg.** { *; }
