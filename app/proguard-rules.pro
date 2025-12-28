# Project specific ProGuard rules

# Keep your data models and everything in the model package
-keep class com.anxiousflyer.peacefulflight.model.** { *; }
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Retrofit 2 rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations, RuntimeInvisibleParameterAnnotations
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Keep the weather service interface explicitly
-keep interface com.anxiousflyer.peacefulflight.data.weather.WeatherService { *; }

# Gson rules
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keepattributes Signature
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OKHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Coroutines - CRITICAL for suspend functions in Retrofit
-keepnames class kotlinx.coroutines.internal.MainDispatcherLoader {}
-keepnames class kotlinx.coroutines.CoroutineScope {}
-keepclassmembers class * extends kotlin.coroutines.jvm.internal.ContinuationImpl {
    <fields>;
}
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Koin rules
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Room entities/DAOs
-keep class com.anxiousflyer.peacefulflight.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep generic info for reflection
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod