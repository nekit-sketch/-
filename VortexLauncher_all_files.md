# Vortex Launcher: все файлы проекта

Создавайте каждый файл строго по указанному пути (каждая папка отдельный уровень).

## Дерево

```
.github/workflows/build.yml
.gitignore
app/build.gradle.kts
app/proguard-rules.pro
app/src/main/AndroidManifest.xml
app/src/main/java/com/vortex/launcher/MainActivity.kt
app/src/main/java/com/vortex/launcher/VortexApp.kt
app/src/main/java/com/vortex/launcher/data/AppIconFetcher.kt
app/src/main/java/com/vortex/launcher/data/AppRepository.kt
app/src/main/java/com/vortex/launcher/data/Models.kt
app/src/main/java/com/vortex/launcher/data/SettingsRepository.kt
app/src/main/java/com/vortex/launcher/data/WeatherRepository.kt
app/src/main/java/com/vortex/launcher/data/db/ProfileDao.kt
app/src/main/java/com/vortex/launcher/data/db/ProfileEntity.kt
app/src/main/java/com/vortex/launcher/data/db/VortexDatabase.kt
app/src/main/java/com/vortex/launcher/di/AppModule.kt
app/src/main/java/com/vortex/launcher/ui/LauncherRoot.kt
app/src/main/java/com/vortex/launcher/ui/MainViewModel.kt
app/src/main/java/com/vortex/launcher/ui/components/Clock.kt
app/src/main/java/com/vortex/launcher/ui/components/LiquidGlass.kt
app/src/main/java/com/vortex/launcher/ui/components/Media.kt
app/src/main/java/com/vortex/launcher/ui/components/TvFocusable.kt
app/src/main/java/com/vortex/launcher/ui/control/ControlCenter.kt
app/src/main/java/com/vortex/launcher/ui/gaming/GamingHomeScreen.kt
app/src/main/java/com/vortex/launcher/ui/home/AllAppsScreen.kt
app/src/main/java/com/vortex/launcher/ui/home/ClassicHomeScreen.kt
app/src/main/java/com/vortex/launcher/ui/onboarding/OnboardingScreen.kt
app/src/main/java/com/vortex/launcher/ui/settings/SettingsScreen.kt
app/src/main/java/com/vortex/launcher/ui/theme/Theme.kt
app/src/main/java/com/vortex/launcher/ui/transition/ModeTransition.kt
app/src/main/res/drawable/banner.xml
app/src/main/res/drawable/ic_launcher_background.xml
app/src/main/res/drawable/ic_launcher_foreground.xml
app/src/main/res/mipmap-anydpi/ic_launcher.xml
app/src/main/res/values/strings.xml
app/src/main/res/values/themes.xml
build.gradle.kts
gradle.properties
settings.gradle.kts
```


## `.github/workflows/build.yml`

```yaml
name: Build APK

on:
  push:
    branches: [ main, master ]
  pull_request:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle 8.7
        uses: gradle/actions/setup-gradle@v3
        with:
          gradle-version: 8.7

      - name: Build debug APK
        run: gradle :app:assembleDebug --stacktrace --no-daemon

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: VortexLauncher-debug
          path: app/build/outputs/apk/debug/*.apk
          if-no-files-found: error
```

## `.gitignore`

```
*.iml
.gradle/
build/
local.properties
.idea/
.DS_Store
captures/
.externalNativeBuild/
.cxx/
```

## `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.vortex.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vortex.launcher"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Подпись debug-ключом, чтобы release APK можно было сразу установить на ТВ
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

## `app/proguard-rules.pro`

```
# Release собирается без minify, правила не требуются.
```

## `app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-feature
        android:name="android.hardware.touchscreen"
        android:required="false" />
    <uses-feature
        android:name="android.software.leanback"
        android:required="false" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <queries>
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
        </intent>
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
    </queries>

    <application
        android:name=".VortexApp"
        android:allowBackup="false"
        android:banner="@drawable/banner"
        android:hardwareAccelerated="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.Vortex">

        <activity
            android:name=".MainActivity"
            android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|keyboard|keyboardHidden|navigation|uiMode|density"
            android:exported="true"
            android:launchMode="singleTask"
            android:screenOrientation="landscape"
            android:stateNotNeeded="true">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
            </intent-filter>

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

## `app/src/main/java/com/vortex/launcher/MainActivity.kt`

```kotlin
package com.vortex.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vortex.launcher.ui.LauncherRoot
import com.vortex.launcher.ui.MainViewModel
import com.vortex.launcher.ui.theme.VortexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // skinType читается из DataStore внутри MainViewModel и применяется в LauncherRoot
        // до первого кадра: Гейминг-режим сохраняется после выключения ТВ.
        setContent {
            VortexTheme {
                LauncherRoot(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshApps()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Нажатие HOME: закрыть панели и вернуться на главный экран
        viewModel.onHomePressed()
    }
}
```

## `app/src/main/java/com/vortex/launcher/VortexApp.kt`

```kotlin
package com.vortex.launcher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.memory.MemoryCache
import coil.request.Options
import com.vortex.launcher.data.AppIconData
import com.vortex.launcher.data.AppIconFetcher
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VortexApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val appContext = applicationContext
        val keyer: Keyer<AppIconData> = object : Keyer<AppIconData> {
            override fun key(data: AppIconData, options: Options): String = "appicon:${data.packageName}"
        }
        val factory: Fetcher.Factory<AppIconData> = AppIconFetcher.Factory(appContext.packageManager)
        return ImageLoader.Builder(appContext)
            .components {
                add(keyer)
                add(factory)
            }
            .memoryCache {
                MemoryCache.Builder(appContext).maxSizePercent(0.15).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(appContext.cacheDir.resolve("coil"))
                    .maxSizeBytes(64L * 1024L * 1024L)
                    .build()
            }
            .crossfade(180)
            .allowRgb565(true)
            .respectCacheHeaders(false)
            .build()
    }
}
```

## `app/src/main/java/com/vortex/launcher/data/AppIconFetcher.kt`

```kotlin
package com.vortex.launcher.data

import android.content.pm.PackageManager
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

/** Лениво достаёт иконку приложения через Coil, чтобы не держать все Drawable в памяти. */
class AppIconFetcher(
    private val data: AppIconData,
    private val packageManager: PackageManager
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val drawable = try {
            packageManager.getApplicationIcon(data.packageName)
        } catch (e: Exception) {
            return null
        }
        return DrawableResult(
            drawable = drawable,
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory(private val packageManager: PackageManager) : Fetcher.Factory<AppIconData> {
        override fun create(data: AppIconData, options: Options, imageLoader: ImageLoader): Fetcher =
            AppIconFetcher(data, packageManager)
    }
}
```

## `app/src/main/java/com/vortex/launcher/data/AppRepository.kt`

```kotlin
package com.vortex.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun loadApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val regular = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val all = pm.queryIntentActivities(leanback, 0) + pm.queryIntentActivities(regular, 0)
        all.asSequence()
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map { info ->
                AppInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    isGame = info.activityInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    fun launch(packageName: String): Boolean {
        val pm = context.packageManager
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
            ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // нет экрана информации о приложении на этом устройстве
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/data/Models.kt`

```kotlin
package com.vortex.launcher.data

enum class SkinType { CLASSIC, GAMING }

enum class ClockPosition { TOP_START, TOP_CENTER, TOP_END }

data class LauncherSettings(
    val onboardingDone: Boolean = false,
    val skinType: SkinType = SkinType.CLASSIC,
    val wallpaper: String = "stock:0",
    val clockPosition: ClockPosition = ClockPosition.TOP_START,
    val showClock: Boolean = true,
    val dockApps: List<String> = emptyList(),
    val showWeather: Boolean = true,
    val weatherCity: String = "Москва",
    val weatherLat: Double = 55.7558,
    val weatherLon: Double = 37.6173,
    val activeProfileId: Long = -1L,
    val recentApps: List<String> = emptyList()
)

data class AppInfo(
    val label: String,
    val packageName: String,
    val isGame: Boolean
)

data class AppIconData(val packageName: String)

data class WeatherInfo(val temp: Int, val text: String)

data class GeoResult(val name: String, val lat: Double, val lon: Double)

/** Первые [count] приложений дока: сохранённые пользователем + добор из общего списка. */
fun resolveDock(saved: List<String>, apps: List<AppInfo>, count: Int = 5): List<AppInfo> {
    val byPackage = apps.associateBy { it.packageName }
    val chosen = saved.mapNotNull { byPackage[it] }.distinctBy { it.packageName }
    val rest = apps.filter { app -> chosen.none { it.packageName == app.packageName } }
    return (chosen + rest).take(count)
}
```

## `app/src/main/java/com/vortex/launcher/data/SettingsRepository.kt`

```kotlin
package com.vortex.launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.vortexStore: DataStore<Preferences> by preferencesDataStore(name = "vortex_settings")

private fun String?.toPackageList(): List<String> =
    this?.split("|")?.filter { it.isNotBlank() } ?: emptyList()

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING = booleanPreferencesKey("onboarding_done")
        val SKIN = stringPreferencesKey("skin_type")
        val WALLPAPER = stringPreferencesKey("wallpaper")
        val CLOCK_POSITION = stringPreferencesKey("clock_position")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val DOCK = stringPreferencesKey("dock_apps")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val CITY = stringPreferencesKey("weather_city")
        val LAT = doublePreferencesKey("weather_lat")
        val LON = doublePreferencesKey("weather_lon")
        val PROFILE = longPreferencesKey("active_profile")
        val RECENT = stringPreferencesKey("recent_apps")
    }

    val settings: Flow<LauncherSettings> = context.vortexStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { p ->
            val d = LauncherSettings()
            LauncherSettings(
                onboardingDone = p[Keys.ONBOARDING] ?: d.onboardingDone,
                skinType = p[Keys.SKIN]?.let { runCatching { SkinType.valueOf(it) }.getOrNull() } ?: d.skinType,
                wallpaper = p[Keys.WALLPAPER] ?: d.wallpaper,
                clockPosition = p[Keys.CLOCK_POSITION]?.let { runCatching { ClockPosition.valueOf(it) }.getOrNull() }
                    ?: d.clockPosition,
                showClock = p[Keys.SHOW_CLOCK] ?: d.showClock,
                dockApps = p[Keys.DOCK].toPackageList(),
                showWeather = p[Keys.SHOW_WEATHER] ?: d.showWeather,
                weatherCity = p[Keys.CITY] ?: d.weatherCity,
                weatherLat = p[Keys.LAT] ?: d.weatherLat,
                weatherLon = p[Keys.LON] ?: d.weatherLon,
                activeProfileId = p[Keys.PROFILE] ?: d.activeProfileId,
                recentApps = p[Keys.RECENT].toPackageList()
            )
        }

    suspend fun setOnboardingDone(done: Boolean) {
        context.vortexStore.edit { it[Keys.ONBOARDING] = done }
    }

    suspend fun setSkin(skin: SkinType) {
        context.vortexStore.edit { it[Keys.SKIN] = skin.name }
    }

    suspend fun setWallpaper(value: String) {
        context.vortexStore.edit { it[Keys.WALLPAPER] = value }
    }

    suspend fun setClockPosition(position: ClockPosition) {
        context.vortexStore.edit { it[Keys.CLOCK_POSITION] = position.name }
    }

    suspend fun setShowClock(show: Boolean) {
        context.vortexStore.edit { it[Keys.SHOW_CLOCK] = show }
    }

    suspend fun setDock(packages: List<String>) {
        context.vortexStore.edit { it[Keys.DOCK] = packages.joinToString("|") }
    }

    suspend fun setShowWeather(show: Boolean) {
        context.vortexStore.edit { it[Keys.SHOW_WEATHER] = show }
    }

    suspend fun setWeatherLocation(city: String, lat: Double, lon: Double) {
        context.vortexStore.edit {
            it[Keys.CITY] = city
            it[Keys.LAT] = lat
            it[Keys.LON] = lon
        }
    }

    suspend fun setActiveProfile(id: Long) {
        context.vortexStore.edit { it[Keys.PROFILE] = id }
    }

    suspend fun pushRecent(packageName: String) {
        context.vortexStore.edit { p ->
            val current = p[Keys.RECENT].toPackageList()
            p[Keys.RECENT] = (listOf(packageName) + current).distinct().take(8).joinToString("|")
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/data/WeatherRepository.kt`

```kotlin
package com.vortex.launcher.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/** Open-Meteo: бесплатный API без ключа. */
@Singleton
class WeatherRepository @Inject constructor() {

    suspend fun fetch(lat: Double, lon: Double): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val body = httpGet(
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
            )
            val current = JSONObject(body).getJSONObject("current")
            WeatherInfo(
                temp = Math.round(current.getDouble("temperature_2m")).toInt(),
                text = describe(current.getInt("weather_code"))
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun geocode(name: String): GeoResult? = withContext(Dispatchers.IO) {
        try {
            val query = URLEncoder.encode(name, "UTF-8")
            val body = httpGet("https://geocoding-api.open-meteo.com/v1/search?name=$query&count=1&language=ru")
            val results = JSONObject(body).optJSONArray("results")
            if (results == null || results.length() == 0) {
                null
            } else {
                val first = results.getJSONObject(0)
                GeoResult(first.getString("name"), first.getDouble("latitude"), first.getDouble("longitude"))
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun httpGet(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        try {
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun describe(code: Int): String = when (code) {
        0 -> "Ясно"
        1, 2 -> "Малооблачно"
        3 -> "Облачно"
        45, 48 -> "Туман"
        in 51..57 -> "Морось"
        in 61..67 -> "Дождь"
        in 71..77 -> "Снег"
        in 80..82 -> "Ливень"
        85, 86 -> "Снегопад"
        in 95..99 -> "Гроза"
        else -> "Облачно"
    }
}
```

## `app/src/main/java/com/vortex/launcher/data/db/ProfileDao.kt`

```kotlin
package com.vortex.launcher.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY id")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int

    @Query("SELECT * FROM profiles ORDER BY id LIMIT 1")
    suspend fun firstOrNull(): ProfileEntity?

    @Insert
    suspend fun insert(profile: ProfileEntity): Long

    @Query("UPDATE profiles SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("UPDATE profiles SET avatarUri = :avatarUri WHERE id = :id")
    suspend fun setAvatar(id: Long, avatarUri: String?)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun delete(id: Long)
}
```

## `app/src/main/java/com/vortex/launcher/data/db/ProfileEntity.kt`

```kotlin
package com.vortex.launcher.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarUri: String? = null
)
```

## `app/src/main/java/com/vortex/launcher/data/db/VortexDatabase.kt`

```kotlin
package com.vortex.launcher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class VortexDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
```

## `app/src/main/java/com/vortex/launcher/di/AppModule.kt`

```kotlin
package com.vortex.launcher.di

import android.content.Context
import androidx.room.Room
import com.vortex.launcher.data.db.ProfileDao
import com.vortex.launcher.data.db.VortexDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VortexDatabase =
        Room.databaseBuilder(context, VortexDatabase::class.java, "vortex.db").build()

    @Provides
    fun provideProfileDao(db: VortexDatabase): ProfileDao = db.profileDao()
}
```

## `app/src/main/java/com/vortex/launcher/ui/LauncherRoot.kt`

```kotlin
package com.vortex.launcher.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vortex.launcher.data.LauncherSettings
import com.vortex.launcher.data.SkinType
import com.vortex.launcher.ui.components.requestSafely
import com.vortex.launcher.ui.control.ControlCenterLayer
import com.vortex.launcher.ui.gaming.GamingHomeScreen
import com.vortex.launcher.ui.home.AllAppsScreen
import com.vortex.launcher.ui.home.ClassicHomeScreen
import com.vortex.launcher.ui.onboarding.OnboardingScreen
import com.vortex.launcher.ui.settings.SettingsScreen
import com.vortex.launcher.ui.transition.ModeTransitionOverlay
import kotlinx.coroutines.delay

private enum class Screen { HOME, ALL_APPS, SETTINGS }

@Composable
fun LauncherRoot(vm: MainViewModel) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val current = settings
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Пока DataStore не прочитан, показываем чёрный кадр: сохранённый skinType применяется сразу
        if (current != null) {
            if (current.onboardingDone) {
                LauncherContent(vm, current)
            } else {
                OnboardingScreen(vm, current)
            }
        }
    }
}

@Composable
private fun LauncherContent(vm: MainViewModel, settings: LauncherSettings) {
    val context = LocalContext.current
    val apps by vm.apps.collectAsStateWithLifecycle()
    val weather by vm.weather.collectAsStateWithLifecycle()
    val profiles by vm.profiles.collectAsStateWithLifecycle()
    val sleeping by vm.sleeping.collectAsStateWithLifecycle()
    val sleepMinutes by vm.sleepMinutes.collectAsStateWithLifecycle()
    val profile = profiles.firstOrNull { it.id == settings.activeProfileId } ?: profiles.firstOrNull()
    val recents = remember(settings.recentApps, apps) {
        val byPackage = apps.associateBy { it.packageName }
        settings.recentApps.mapNotNull { byPackage[it] }
    }

    // Режим, который сейчас нарисован. Начальное значение = сохранённый skinType (Гейминг не сбрасывается).
    var shownSkin by remember { mutableStateOf(settings.skinType) }
    var transitionTarget by remember { mutableStateOf<SkinType?>(null) }
    var screen by remember { mutableStateOf(Screen.HOME) }
    var controlOpen by remember { mutableStateOf(false) }
    var launching by remember { mutableStateOf<String?>(null) }

    val switchSkin: (SkinType) -> Unit = { target ->
        if (transitionTarget == null && target != shownSkin) {
            controlOpen = false
            screen = Screen.HOME
            transitionTarget = target
            vm.setSkin(target) // сохраняем сразу: выключили ТВ посреди перехода, всё равно запомнится
        }
    }

    LaunchedEffect(Unit) {
        vm.homeEvents.collect {
            screen = Screen.HOME
            controlOpen = false
        }
    }

    LaunchedEffect(launching) {
        val pkg = launching ?: return@LaunchedEffect
        delay(220)
        vm.launchApp(pkg)
        delay(450)
        launching = null
    }
    val launchAlpha by animateFloatAsState(
        targetValue = if (launching != null) 1f else 0f,
        animationSpec = tween(220),
        label = "launchAlpha"
    )

    val blockFocus = controlOpen || transitionTarget != null || launching != null || sleeping

    BackHandler(enabled = true) {
        when {
            transitionTarget != null || sleeping -> Unit
            controlOpen -> controlOpen = false
            screen != Screen.HOME -> screen = Screen.HOME
            else -> Unit
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp && event.key == Key.Menu && transitionTarget == null && !sleeping) {
                    controlOpen = !controlOpen
                    true
                } else {
                    false
                }
            }
    ) {
        // Слой контента
        Box(
            Modifier
                .fillMaxSize()
                .focusProperties { canFocus = !blockFocus }
                .graphicsLayer {
                    val s = 1f + 0.06f * launchAlpha
                    scaleX = s
                    scaleY = s
                }
        ) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = {
                    if (targetState == Screen.HOME) {
                        fadeIn(tween(250)) togetherWith
                            (fadeOut(tween(200)) + scaleOut(animationSpec = tween(250), targetScale = 1.06f))
                    } else {
                        (fadeIn(tween(300)) + scaleIn(
                            initialScale = 0.94f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )) togetherWith fadeOut(tween(200))
                    }
                },
                label = "screens"
            ) { current ->
                when (current) {
                    Screen.HOME -> if (shownSkin == SkinType.CLASSIC) {
                        ClassicHomeScreen(
                            settings = settings,
                            apps = apps,
                            weather = weather,
                            profile = profile,
                            active = !blockFocus,
                            onLaunch = { launching = it },
                            onAllApps = { screen = Screen.ALL_APPS },
                            onControlCenter = { controlOpen = true }
                        )
                    } else {
                        GamingHomeScreen(
                            apps = apps,
                            profile = profile,
                            active = !blockFocus,
                            onLaunch = { launching = it },
                            onAppInfo = { vm.openAppInfo(it) },
                            onAllApps = { screen = Screen.ALL_APPS },
                            onControlCenter = { controlOpen = true }
                        )
                    }
                    Screen.ALL_APPS -> AllAppsScreen(
                        apps = apps,
                        wallpaper = settings.wallpaper,
                        active = !blockFocus,
                        onLaunch = { launching = it }
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        vm = vm,
                        settings = settings,
                        apps = apps,
                        profiles = profiles,
                        active = !blockFocus,
                        onSkinChange = switchSkin
                    )
                }
            }
        }

        // Центр управления: панель справа 36% ширины
        ControlCenterLayer(
            visible = controlOpen && transitionTarget == null,
            currentSkin = shownSkin,
            sleepMinutes = sleepMinutes,
            recents = recents,
            avatarUri = profile?.avatarUri,
            onClose = { controlOpen = false },
            onPower = {
                controlOpen = false
                vm.sleepNow()
            },
            onWifi = { openSystem(context, android.provider.Settings.ACTION_WIFI_SETTINGS) },
            onAudio = { openSystem(context, android.provider.Settings.ACTION_SOUND_SETTINGS) },
            onSleepTimer = { vm.cycleSleepTimer() },
            onSwitchMode = {
                switchSkin(if (shownSkin == SkinType.GAMING) SkinType.CLASSIC else SkinType.GAMING)
            },
            onSettings = {
                controlOpen = false
                screen = Screen.SETTINGS
            },
            onAllApps = {
                controlOpen = false
                screen = Screen.ALL_APPS
            },
            onLaunch = {
                controlOpen = false
                launching = it
            }
        )

        // Затемнение при запуске приложения
        if (launchAlpha > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = launchAlpha }
                    .background(Color.Black)
            )
        }

        // Экран выключен / сработал таймер сна: любая кнопка пульта возвращает
        if (sleeping) {
            val focus = remember { FocusRequester() }
            LaunchedEffect(Unit) { focus.requestSafely(50) }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .focusRequester(focus)
                    .onKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            vm.wake()
                            true
                        } else {
                            false
                        }
                    }
                    .focusable()
            )
        }

        // Анимация перехода между режимами
        transitionTarget?.let { target ->
            ModeTransitionOverlay(
                target = target,
                onMidpoint = { shownSkin = target },
                onFinished = { transitionTarget = null }
            )
        }
    }
}

private fun openSystem(context: Context, action: String) {
    try {
        context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        try {
            context.startActivity(
                Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e2: Exception) {
            // системных настроек нет
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/MainViewModel.kt`

```kotlin
package com.vortex.launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.data.AppRepository
import com.vortex.launcher.data.ClockPosition
import com.vortex.launcher.data.LauncherSettings
import com.vortex.launcher.data.SettingsRepository
import com.vortex.launcher.data.SkinType
import com.vortex.launcher.data.WeatherInfo
import com.vortex.launcher.data.WeatherRepository
import com.vortex.launcher.data.db.ProfileDao
import com.vortex.launcher.data.db.ProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val appRepo: AppRepository,
    private val weatherRepo: WeatherRepository,
    private val profileDao: ProfileDao
) : ViewModel() {

    /** null, пока DataStore не прочитан: UI рисует чёрный кадр и не мигает не тем режимом. */
    val settings: StateFlow<LauncherSettings?> =
        settingsRepo.settings.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val profiles: StateFlow<List<ProfileEntity>> =
        profileDao.observeAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _weather = MutableStateFlow<WeatherInfo?>(null)
    val weather: StateFlow<WeatherInfo?> = _weather.asStateFlow()

    private val _sleepMinutes = MutableStateFlow(0)
    val sleepMinutes: StateFlow<Int> = _sleepMinutes.asStateFlow()

    private val _sleeping = MutableStateFlow(false)
    val sleeping: StateFlow<Boolean> = _sleeping.asStateFlow()

    private val _homeEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homeEvents: SharedFlow<Unit> = _homeEvents.asSharedFlow()

    private var sleepJob: Job? = null

    init {
        refreshApps()
        ensureDefaultProfile()
        startWeatherUpdates()
    }

    // ---------- Приложения ----------

    fun refreshApps() {
        viewModelScope.launch { _apps.value = appRepo.loadApps() }
    }

    fun launchApp(packageName: String) {
        appRepo.launch(packageName)
        viewModelScope.launch { settingsRepo.pushRecent(packageName) }
    }

    fun openAppInfo(packageName: String) = appRepo.openAppInfo(packageName)

    fun onHomePressed() {
        _homeEvents.tryEmit(Unit)
    }

    // ---------- Настройки ----------

    fun completeOnboarding() {
        viewModelScope.launch { settingsRepo.setOnboardingDone(true) }
    }

    fun setSkin(skin: SkinType) {
        viewModelScope.launch { settingsRepo.setSkin(skin) }
    }

    fun setWallpaper(value: String) {
        viewModelScope.launch { settingsRepo.setWallpaper(value) }
    }

    fun setClockPosition(position: ClockPosition) {
        viewModelScope.launch { settingsRepo.setClockPosition(position) }
    }

    fun setShowClock(show: Boolean) {
        viewModelScope.launch { settingsRepo.setShowClock(show) }
    }

    fun setShowWeather(show: Boolean) {
        viewModelScope.launch { settingsRepo.setShowWeather(show) }
    }

    fun setDock(packages: List<String>) {
        viewModelScope.launch { settingsRepo.setDock(packages) }
    }

    fun setCity(name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val geo = weatherRepo.geocode(name.trim())
            if (geo != null) {
                settingsRepo.setWeatherLocation(geo.name, geo.lat, geo.lon)
            }
            onResult(geo != null)
        }
    }

    // ---------- Профили ----------

    private fun ensureDefaultProfile() {
        viewModelScope.launch {
            if (profileDao.count() == 0) {
                profileDao.insert(ProfileEntity(name = "Профиль 1"))
            }
            val current = settingsRepo.settings.first()
            val first = profileDao.firstOrNull()
            if (first != null && current.activeProfileId < 0) {
                settingsRepo.setActiveProfile(first.id)
            }
        }
    }

    fun addProfile() {
        viewModelScope.launch {
            val next = profileDao.count() + 1
            profileDao.insert(ProfileEntity(name = "Профиль $next"))
        }
    }

    fun selectProfile(id: Long) {
        viewModelScope.launch { settingsRepo.setActiveProfile(id) }
    }

    fun renameProfile(id: Long, name: String) {
        viewModelScope.launch { profileDao.rename(id, name) }
    }

    fun setProfileAvatar(id: Long, uri: String?) {
        viewModelScope.launch { profileDao.setAvatar(id, uri) }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch {
            if (profileDao.count() <= 1) return@launch
            profileDao.delete(id)
            if (settingsRepo.settings.first().activeProfileId == id) {
                profileDao.firstOrNull()?.let { settingsRepo.setActiveProfile(it.id) }
            }
        }
    }

    // ---------- Таймер сна / экран выключен ----------

    fun cycleSleepTimer() {
        val options = listOf(0, 15, 30, 60, 90)
        val next = options[(options.indexOf(_sleepMinutes.value) + 1) % options.size]
        _sleepMinutes.value = next
        sleepJob?.cancel()
        if (next > 0) {
            sleepJob = viewModelScope.launch {
                delay(next * 60_000L)
                _sleepMinutes.value = 0
                _sleeping.value = true
            }
        }
    }

    fun sleepNow() {
        _sleeping.value = true
    }

    fun wake() {
        _sleeping.value = false
    }

    // ---------- Погода ----------

    private fun startWeatherUpdates() {
        viewModelScope.launch {
            settingsRepo.settings
                .map { Triple(it.showWeather, it.weatherLat, it.weatherLon) }
                .distinctUntilChanged()
                .collectLatest { (show, lat, lon) ->
                    if (!show) {
                        _weather.value = null
                    } else {
                        while (true) {
                            val info = weatherRepo.fetch(lat, lon)
                            if (info != null) _weather.value = info
                            delay(30 * 60_000L)
                        }
                    }
                }
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/components/Clock.kt`

```kotlin
package com.vortex.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ClockState(val time: String, val date: String, val greeting: String)

private val TimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DateFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("ru"))

private fun currentClock(): ClockState {
    val now = LocalDateTime.now()
    val greeting = when (now.hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..22 -> "Добрый вечер"
        else -> "Доброй ночи"
    }
    return ClockState(
        time = now.format(TimeFormat),
        date = now.format(DateFormat).replaceFirstChar { it.uppercase() },
        greeting = greeting
    )
}

/** Обновляется раз в минуту, ровно на границе минуты. */
@Composable
fun rememberClockState(): State<ClockState> = produceState(initialValue = currentClock()) {
    while (true) {
        value = currentClock()
        delay(60_000L - System.currentTimeMillis() % 60_000L + 100L)
    }
}

@Composable
fun ClockBlock(
    clock: ClockState,
    weatherText: String?,
    name: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val greeting = if (!name.isNullOrBlank()) "${clock.greeting}, $name" else clock.greeting
        Text(
            text = greeting,
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = listOfNotNull(clock.date, weatherText).joinToString("  ·  "),
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 16.sp
        )
        Text(
            text = clock.time,
            style = TextStyle(
                fontSize = 140.sp,
                lineHeight = 140.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-4).sp,
                color = Color.White.copy(alpha = 0.38f)
            )
        )
    }
}

/** Мини-островок Liquid Glass в правом верхнем углу: вызывает Центр управления. */
@Composable
fun ControlIsland(
    timeText: String,
    avatarUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.1f,
        focusRequester = focusRequester
    ) { focused ->
        Row(
            modifier = Modifier
                .liquidGlass(RoundedCornerShape(50), focused)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(timeText, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(10.dp))
            ProfileAvatar(avatarUri, 28.dp)
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/components/LiquidGlass.kt`

```kotlin
package com.vortex.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Liquid Glass без тяжёлого blur: полупрозрачная подложка + градиентная белая обводка 1dp.
 * Дёшево для слабого железа (Сбербокс): никаких RenderEffect на больших областях.
 */
fun Modifier.liquidGlass(shape: Shape, focused: Boolean = false): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (focused) 0.30f else 0.14f),
                Color.White.copy(alpha = if (focused) 0.16f else 0.06f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = if (focused) 1f else 0.60f),
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = if (focused) 0.80f else 0.40f)
            )
        ),
        shape = shape
    )

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    focusRequester: FocusRequester? = null,
    onFocused: (() -> Unit)? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.08f,
        focusRequester = focusRequester,
        onFocused = onFocused
    ) { focused ->
        Row(
            modifier = Modifier
                .liquidGlass(RoundedCornerShape(26.dp), focused)
                .padding(horizontal = 26.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun GlassCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    focusRequester: FocusRequester? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.14f,
        focusRequester = focusRequester
    ) { focused ->
        Box(
            modifier = Modifier
                .size(size)
                .liquidGlass(CircleShape, focused),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.46f))
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/components/Media.kt`

```kotlin
package com.vortex.launcher.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vortex.launcher.data.AppIconData

/** Иконка приложения через Coil: кэш в памяти по ключу + заглушка. */
@Composable
fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val request = remember(packageName) {
        ImageRequest.Builder(context)
            .data(AppIconData(packageName))
            .memoryCacheKey("appicon:$packageName")
            .size(160)
            .build()
    }
    AsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier,
        placeholder = ColorPainter(Color(0x22FFFFFF)),
        error = ColorPainter(Color(0x22FFFFFF)),
        contentScale = ContentScale.Fit
    )
}

/** Аватар профиля. Без картинки: минималистичная чёрно-белая иконка. */
@Composable
fun ProfileAvatar(
    avatarUri: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF101010))
            .border(if (highlight) 2.dp else 1.dp, Color.White.copy(alpha = if (highlight) 1f else 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(size * 0.6f)
            )
        } else {
            val request = remember(avatarUri) {
                ImageRequest.Builder(context)
                    .data(Uri.parse(avatarUri))
                    .size(256)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                placeholder = ColorPainter(Color(0xFF101010)),
                error = ColorPainter(Color(0xFF101010)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

object StockWallpapers {
    val names = listOf("Океан", "Аврора", "Эмбер", "Полночь", "Фиалка")

    // [0] фон, [1] и [2] подсветки, [3] фон-конец
    val palettes: List<List<Color>> = listOf(
        listOf(Color(0xFF0A1F5C), Color(0xFF1B6CF2), Color(0xFF7B2FF7), Color(0xFF00A6D6)),
        listOf(Color(0xFF021B2B), Color(0xFF0F9D8A), Color(0xFF5B3DF5), Color(0xFF0B1C4D)),
        listOf(Color(0xFF1A0510), Color(0xFFB3265E), Color(0xFFFF7A59), Color(0xFF3B0A5C)),
        listOf(Color(0xFF05070F), Color(0xFF11255A), Color(0xFF2A46A8), Color(0xFF0A0F2C)),
        listOf(Color(0xFF12062B), Color(0xFF5B1FB8), Color(0xFFE0529C), Color(0xFF1B0B45))
    )

    /** Тёмно-синяя палитра в стиле PS5 */
    val ps5: List<Color> = listOf(Color(0xFF020410), Color(0xFF1250C8), Color(0xFF0A2A6E), Color(0xFF050A1C))

    fun palette(index: Int): List<Color> = palettes[index.coerceIn(0, palettes.lastIndex)]
}

/** Живой переливающийся градиент. Меняется только фаза: перерисовывается один Canvas. */
@Composable
fun AnimatedGradient(
    colors: List<Color>,
    modifier: Modifier = Modifier.fillMaxSize(),
    animated: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "gradient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )
    Canvas(modifier) {
        val p = if (animated) phase else 0.4f
        val w = size.width
        val h = size.height
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(colors[0], colors[3]),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )
        val radius = h * 0.9f
        val c1 = Offset(w * (0.15f + 0.55f * p), h * (0.25f + 0.30f * p))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[1].copy(alpha = 0.75f), Color.Transparent),
                center = c1,
                radius = radius
            ),
            radius = radius,
            center = c1
        )
        val c2 = Offset(w * (0.85f - 0.50f * p), h * (0.85f - 0.35f * p))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors[2].copy(alpha = 0.65f), Color.Transparent),
                center = c2,
                radius = radius
            ),
            radius = radius,
            center = c2
        )
    }
}

/** Обои: "stock:N" (живой градиент) или "uri:..." (свои, через Coil с ограничением размера). */
@Composable
fun WallpaperBackground(
    wallpaper: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    animated: Boolean = true
) {
    if (wallpaper.startsWith("uri:")) {
        val context = LocalContext.current
        val request = remember(wallpaper) {
            ImageRequest.Builder(context)
                .data(Uri.parse(wallpaper.removePrefix("uri:")))
                .size(1920, 1080)
                .crossfade(true)
                .build()
        }
        AsyncImage(
            model = request,
            contentDescription = null,
            modifier = modifier,
            placeholder = ColorPainter(Color.Black),
            error = ColorPainter(Color(0xFF0B0F24)),
            contentScale = ContentScale.Crop
        )
    } else {
        val index = wallpaper.removePrefix("stock:").toIntOrNull() ?: 0
        AnimatedGradient(StockWallpapers.palette(index), modifier, animated)
    }
}

/** Системный выбор изображения. На ТВ без файлового менеджера показывает сообщение вместо падения. */
@Composable
fun rememberImagePicker(onPicked: (Uri) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                // разрешение не персистентное: используем одноразовый доступ
            }
            onPicked(uri)
        }
    }
    return remember(launcher) {
        {
            try {
                launcher.launch(arrayOf("image/*"))
            } catch (e: Exception) {
                Toast.makeText(context, "Выбор файлов недоступен на этом устройстве", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/components/TvFocusable.kt`

```kotlin
package com.vortex.launcher.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/** Безопасный запрос фокуса: ждём раскладку, не падаем, если узел ещё не прикреплён. */
suspend fun FocusRequester.requestSafely(delayMs: Long = 80L) {
    delay(delayMs)
    try {
        requestFocus()
    } catch (e: Exception) {
        // узел ещё не в иерархии
    }
}

/**
 * Базовый D-pad элемент: фокус + spring-масштаб через graphicsLayer (без перерисовки остального UI).
 */
@Composable
fun TvFocusable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusScale: Float = 1.12f,
    focusRequester: FocusRequester? = null,
    onFocused: (() -> Unit)? = null,
    content: @Composable BoxScope.(focused: Boolean) -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) focusScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "focusScale"
    )
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .zIndex(if (focused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) onFocused?.invoke()
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
    ) {
        content(this, focused)
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/control/ControlCenter.kt`

```kotlin
package com.vortex.launcher.ui.control

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.data.SkinType
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.ProfileAvatar
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.rememberClockState
import com.vortex.launcher.ui.components.requestSafely

/**
 * Центр управления: выезжающая справа панель шириной 36% экрана.
 * В Гейминг-режиме нижняя кнопка возвращает в классический режим, в классике открывает Гейминг.
 */
@Composable
fun ControlCenterLayer(
    visible: Boolean,
    currentSkin: SkinType,
    sleepMinutes: Int,
    recents: List<AppInfo>,
    avatarUri: String?,
    onClose: () -> Unit,
    onPower: () -> Unit,
    onWifi: () -> Unit,
    onAudio: () -> Unit,
    onSleepTimer: () -> Unit,
    onSwitchMode: () -> Unit,
    onSettings: () -> Unit,
    onAllApps: () -> Unit,
    onLaunch: (String) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200))
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally(
                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
            ) { fullWidth -> fullWidth } + fadeIn(tween(200)),
            exit = slideOutHorizontally(animationSpec = tween(220)) { fullWidth -> fullWidth } + fadeOut(tween(180))
        ) {
            Panel(
                currentSkin = currentSkin,
                sleepMinutes = sleepMinutes,
                recents = recents,
                avatarUri = avatarUri,
                onClose = onClose,
                onPower = onPower,
                onWifi = onWifi,
                onAudio = onAudio,
                onSleepTimer = onSleepTimer,
                onSwitchMode = onSwitchMode,
                onSettings = onSettings,
                onAllApps = onAllApps,
                onLaunch = onLaunch
            )
        }
    }
}

@Composable
private fun Panel(
    currentSkin: SkinType,
    sleepMinutes: Int,
    recents: List<AppInfo>,
    avatarUri: String?,
    onClose: () -> Unit,
    onPower: () -> Unit,
    onWifi: () -> Unit,
    onAudio: () -> Unit,
    onSleepTimer: () -> Unit,
    onSwitchMode: () -> Unit,
    onSettings: () -> Unit,
    onAllApps: () -> Unit,
    onLaunch: (String) -> Unit
) {
    val clock by rememberClockState()
    val wifiConnected by rememberNetworkConnected()
    var recentOpen by remember { mutableStateOf(false) }
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstFocus.requestSafely(180) }

    val shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.36f)
            .clip(shape)
            .background(Color(0xE60E1220))
            .liquidGlass(shape)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(clock.time, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            ProfileAvatar(avatarUri, 34.dp)
        }

        Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ControlTile(
                icon = Icons.Filled.PowerSettingsNew,
                label = "Power",
                big = true,
                onClick = onPower,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                focusRequester = firstFocus
            )
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ControlTile(
                    icon = if (wifiConnected) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                    label = if (wifiConnected) "Wi-Fi\nConnected" else "Wi-Fi\nOff",
                    onClick = onWifi,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                ControlTile(
                    icon = Icons.Filled.FilterNone,
                    label = "Recent\nApps",
                    highlighted = recentOpen,
                    onClick = { recentOpen = !recentOpen },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }
        }

        Row(Modifier.fillMaxWidth().height(72.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ControlTile(
                icon = Icons.Filled.Speaker,
                label = "Audio\nHDMI",
                onClick = onAudio,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            ControlTile(
                icon = Icons.Filled.Timer,
                label = if (sleepMinutes > 0) "Sleep\n$sleepMinutes min" else "Sleep\nTimer",
                highlighted = sleepMinutes > 0,
                onClick = onSleepTimer,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        if (recentOpen) {
            if (recents.isEmpty()) {
                Text("Нет недавних приложений", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().height(72.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(recents, key = { it.packageName }) { app ->
                        TvFocusable(onClick = { onLaunch(app.packageName) }, focusScale = 1.2f) { focused ->
                            val iconShape = RoundedCornerShape(10.dp)
                            AppIcon(
                                packageName = app.packageName,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(iconShape)
                                    .then(if (focused) Modifier.border(2.dp, Color.White, iconShape) else Modifier)
                            )
                        }
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            CircleAction(Icons.Filled.Settings, "Настройки", onSettings)
            if (currentSkin == SkinType.GAMING) {
                CircleAction(Icons.Filled.Home, "Классика", onSwitchMode)
            } else {
                CircleAction(Icons.Filled.SportsEsports, "Гейминг", onSwitchMode)
            }
            CircleAction(Icons.Filled.Apps, "Все приложения", onAllApps)
            CircleAction(Icons.Filled.Close, "Закрыть", onClose)
        }
    }
}

@Composable
private fun ControlTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    big: Boolean = false,
    highlighted: Boolean = false,
    focusRequester: FocusRequester? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.05f,
        focusRequester = focusRequester
    ) { focused ->
        val shape = RoundedCornerShape(if (big) 28.dp else 24.dp)
        val foreground = if (highlighted) Color(0xFF101420) else Color.White
        val base = if (highlighted) {
            Modifier.fillMaxSize().clip(shape).background(Color.White.copy(alpha = 0.92f))
        } else {
            Modifier.fillMaxSize().liquidGlass(shape, focused)
        }
        Box(base.then(if (highlighted && focused) Modifier.border(2.dp, Color(0xFF9FC4FF), shape) else Modifier)) {
            if (big) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(44.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(label, color = foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                Row(
                    Modifier.align(Alignment.Center).padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(label, color = foreground, fontSize = 13.sp, lineHeight = 15.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun CircleAction(icon: ImageVector, caption: String, onClick: () -> Unit) {
    TvFocusable(onClick = onClick, focusScale = 1.12f) { focused ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(50.dp).liquidGlass(CircleShape, focused),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = caption, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(
                caption,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun rememberNetworkConnected(): State<Boolean> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(isNetworkConnected(context)) }
    DisposableEffect(Unit) {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                state.value = isNetworkConnected(context)
            }

            override fun onLost(network: Network) {
                state.value = isNetworkConnected(context)
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                state.value = isNetworkConnected(context)
            }
        }
        try {
            manager?.registerDefaultNetworkCallback(callback)
        } catch (e: Exception) {
            // нет доступа к состоянию сети
        }
        onDispose {
            try {
                manager?.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                // уже снят
            }
        }
    }
    return state
}

private fun isNetworkConnected(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
    val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}
```

## `app/src/main/java/com/vortex/launcher/ui/gaming/GamingHomeScreen.kt`

```kotlin
package com.vortex.launcher.ui.gaming

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.data.db.ProfileEntity
import com.vortex.launcher.ui.components.AnimatedGradient
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.ControlIsland
import com.vortex.launcher.ui.components.GlassButton
import com.vortex.launcher.ui.components.GlassCircleButton
import com.vortex.launcher.ui.components.StockWallpapers
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.rememberClockState
import com.vortex.launcher.ui.components.requestSafely

/**
 * Vortex Gaming: только вкладка «Игры», карусель квадратных иконок (LazyRow),
 * живой PS5-градиент вместо статичных обоев, мини-островок Центра управления справа сверху.
 */
@Composable
fun GamingHomeScreen(
    apps: List<AppInfo>,
    profile: ProfileEntity?,
    active: Boolean,
    onLaunch: (String) -> Unit,
    onAppInfo: (String) -> Unit,
    onAllApps: () -> Unit,
    onControlCenter: () -> Unit
) {
    val games = remember(apps) { apps.filter { it.isGame }.ifEmpty { apps } }
    var focusedIndex by remember { mutableIntStateOf(0) }
    val selected = games.getOrNull(focusedIndex)
    val first = remember { FocusRequester() }
    LaunchedEffect(active, games.isNotEmpty()) {
        if (active && games.isNotEmpty()) first.requestSafely(150)
    }
    val clock by rememberClockState()

    Box(Modifier.fillMaxSize().background(Color(0xFF020410))) {
        AnimatedGradient(StockWallpapers.ps5)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.50f))
                    )
                )
        )

        // Вкладка «Игры» (вкладки «Мультимедиа» нет)
        Column(Modifier.align(Alignment.TopStart).padding(start = 56.dp, top = 30.dp)) {
            Text("Игры", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Box(Modifier.width(28.dp).height(3.dp).clip(CircleShape).background(Color.White))
        }

        ControlIsland(
            timeText = clock.time,
            avatarUri = profile?.avatarUri,
            onClick = onControlCenter,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 24.dp, end = 40.dp)
        )

        // Карусель квадратных иконок игр
        LazyRow(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 84.dp).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(games, key = { _, game -> game.packageName }) { index, game ->
                TvFocusable(
                    onClick = { onLaunch(game.packageName) },
                    focusScale = 1.32f,
                    focusRequester = if (index == 0) first else null,
                    onFocused = { focusedIndex = index }
                ) { focused ->
                    val shape = RoundedCornerShape(16.dp)
                    AppIcon(
                        packageName = game.packageName,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(shape)
                            .then(if (focused) Modifier.border(2.dp, Color.White, shape) else Modifier)
                    )
                }
            }
            item(key = "all-apps") {
                TvFocusable(
                    onClick = onAllApps,
                    focusScale = 1.32f,
                    onFocused = { focusedIndex = games.size }
                ) { focused ->
                    Box(
                        modifier = Modifier.size(84.dp).liquidGlass(RoundedCornerShape(16.dp), focused),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Apps, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                    }
                }
            }
        }

        // Заголовок выбранной игры + кнопки (блока достижений нет)
        Column(Modifier.align(Alignment.BottomStart).padding(start = 56.dp, bottom = 64.dp)) {
            Crossfade(targetState = selected?.label ?: "Все приложения", label = "gameTitle") { title ->
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 560.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            if (selected != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassButton(
                        text = "Играть",
                        icon = Icons.Filled.PlayArrow,
                        onClick = { onLaunch(selected.packageName) }
                    )
                    Spacer(Modifier.width(12.dp))
                    GlassCircleButton(
                        icon = Icons.Filled.MoreHoriz,
                        onClick = { onAppInfo(selected.packageName) },
                        size = 50.dp
                    )
                }
            }
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/home/AllAppsScreen.kt`

```kotlin
package com.vortex.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.WallpaperBackground
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.requestSafely

/** Экран «Все приложения»: LazyVerticalGrid с прямоугольными плитками Liquid Glass. */
@Composable
fun AllAppsScreen(
    apps: List<AppInfo>,
    wallpaper: String,
    active: Boolean,
    onLaunch: (String) -> Unit
) {
    val first = remember { FocusRequester() }
    LaunchedEffect(active, apps.isNotEmpty()) {
        if (active && apps.isNotEmpty()) first.requestSafely(150)
    }

    Box(Modifier.fillMaxSize()) {
        WallpaperBackground(wallpaper, animated = false)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
        Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 32.dp)) {
            Text("Все приложения", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                    AppTile(app, if (index == 0) first else null) { onLaunch(app.packageName) }
                }
            }
        }
    }
}

@Composable
private fun AppTile(app: AppInfo, focusRequester: FocusRequester?, onClick: () -> Unit) {
    TvFocusable(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        focusScale = 1.08f,
        focusRequester = focusRequester
    ) { focused ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .liquidGlass(RoundedCornerShape(16.dp), focused)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppIcon(app.packageName, Modifier.size(52.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/home/ClassicHomeScreen.kt`

```kotlin
package com.vortex.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.data.ClockPosition
import com.vortex.launcher.data.LauncherSettings
import com.vortex.launcher.data.WeatherInfo
import com.vortex.launcher.data.db.ProfileEntity
import com.vortex.launcher.data.resolveDock
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.ClockBlock
import com.vortex.launcher.ui.components.ControlIsland
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.WallpaperBackground
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.rememberClockState
import com.vortex.launcher.ui.components.requestSafely

/** Классический режим: обои, большие часы, док из 5 приложений и кнопка «Все приложения». */
@Composable
fun ClassicHomeScreen(
    settings: LauncherSettings,
    apps: List<AppInfo>,
    weather: WeatherInfo?,
    profile: ProfileEntity?,
    active: Boolean,
    onLaunch: (String) -> Unit,
    onAllApps: () -> Unit,
    onControlCenter: () -> Unit
) {
    val dock = remember(settings.dockApps, apps) { resolveDock(settings.dockApps, apps) }
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(active, dock.isNotEmpty()) {
        if (active && dock.isNotEmpty()) firstFocus.requestSafely(120)
    }
    val clock by rememberClockState()
    val weatherText = if (settings.showWeather && weather != null) "${weather.temp}° ${weather.text}" else null

    Box(Modifier.fillMaxSize()) {
        WallpaperBackground(settings.wallpaper)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.40f))
                    )
                )
        )

        if (settings.showClock) {
            val (alignment, topPadding) = when (settings.clockPosition) {
                ClockPosition.TOP_START -> Alignment.TopStart to 36.dp
                ClockPosition.TOP_CENTER -> Alignment.TopCenter to 36.dp
                ClockPosition.TOP_END -> Alignment.TopEnd to 84.dp
            }
            ClockBlock(
                clock = clock,
                weatherText = weatherText,
                name = profile?.name,
                modifier = Modifier
                    .align(alignment)
                    .padding(start = 48.dp, end = 48.dp, top = topPadding)
            )
        }

        ControlIsland(
            timeText = clock.time,
            avatarUri = profile?.avatarUri,
            onClick = onControlCenter,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 28.dp, end = 40.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dock(dock, firstFocus, onLaunch)
            Spacer(Modifier.width(20.dp))
            AllAppsButton(onAllApps)
        }
    }
}

@Composable
private fun Dock(apps: List<AppInfo>, firstFocus: FocusRequester, onLaunch: (String) -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Box(contentAlignment = Alignment.Center) {
        // Подложка отдельным слоем, чтобы увеличенные иконки не обрезались clip'ом
        Box(Modifier.matchParentSize().liquidGlass(shape))
        Row(
            modifier = Modifier.padding(horizontal = 26.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            apps.forEachIndexed { index, app ->
                TvFocusable(
                    onClick = { onLaunch(app.packageName) },
                    focusScale = 1.28f,
                    focusRequester = if (index == 0) firstFocus else null
                ) { focused ->
                    val iconShape = RoundedCornerShape(14.dp)
                    AppIcon(
                        packageName = app.packageName,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(iconShape)
                            .then(if (focused) Modifier.border(2.dp, Color.White, iconShape) else Modifier)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllAppsButton(onClick: () -> Unit) {
    TvFocusable(onClick = onClick, focusScale = 1.12f) { focused ->
        Box(
            modifier = Modifier
                .size(104.dp)
                .liquidGlass(RoundedCornerShape(28.dp), focused),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Apps, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/onboarding/OnboardingScreen.kt`

```kotlin
package com.vortex.launcher.ui.onboarding

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vortex.launcher.data.LauncherSettings
import com.vortex.launcher.data.SkinType
import com.vortex.launcher.ui.MainViewModel
import com.vortex.launcher.ui.components.AnimatedGradient
import com.vortex.launcher.ui.components.GlassButton
import com.vortex.launcher.ui.components.ProfileAvatar
import com.vortex.launcher.ui.components.StockWallpapers
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.WallpaperBackground
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.rememberImagePicker
import com.vortex.launcher.ui.components.requestSafely
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private val GlowColors = listOf(
    Color(0xFF4B7BFF),
    Color(0xFF9B5CFF),
    Color(0xFF3DDCFF),
    Color(0xFFFF4F7B)
)

@Composable
fun OnboardingScreen(vm: MainViewModel, settings: LauncherSettings) {
    // 0 Splash, 1 Скин, 2 Обои, 3 По умолчанию, 4 Профиль (шага Wi-Fi нет)
    var step by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = step > 0) { step -= 1 }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (step > 0) {
            WallpaperBackground(settings.wallpaper, animated = false)
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        }
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (fadeIn(tween(350)) + scaleIn(initialScale = 0.94f, animationSpec = spring(stiffness = Spring.StiffnessLow))) togetherWith
                    fadeOut(tween(200))
            },
            label = "onboarding"
        ) { current ->
            when (current) {
                0 -> SplashStep(onStart = { step = 1 })
                1 -> SkinStep(settings, vm, onNext = { step = 2 })
                2 -> WallpaperStep(settings, vm, onNext = { step = 3 })
                3 -> DefaultStep(onNext = { step = 4 })
                else -> ProfileStep(settings, vm)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Каркас шага
// ---------------------------------------------------------------------------------------------

@Composable
private fun StepScaffold(
    title: String,
    subtitle: String?,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 36.dp)) {
        Text(title, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Text(
                subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            content = content
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

// ---------------------------------------------------------------------------------------------
// Шаг 0: стартовая анимация (жидкое свечение собирается в логотип) + кнопка «Начнём»
// ---------------------------------------------------------------------------------------------

@Composable
private fun SplashStep(onStart: () -> Unit) {
    val progress = remember { Animatable(0f) }
    var showButton by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(2400, easing = FastOutSlowInEasing))
        delay(150)
        showButton = true
        focus.requestSafely(350)
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Canvas(Modifier.fillMaxSize()) { drawLiquid(progress.value) }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    val a = ((progress.value - 0.7f) / 0.3f).coerceIn(0f, 1f)
                    alpha = a
                    scaleX = 0.9f + 0.1f * a
                    scaleY = 0.9f + 0.1f * a
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Vortex", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Launcher", color = Color.White.copy(alpha = 0.8f), fontSize = 22.sp, letterSpacing = 10.sp)
        }

        AnimatedVisibility(
            visible = showButton,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp),
            enter = fadeIn(tween(400)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        ) {
            GlassButton(text = "Начнём", onClick = onStart, focusRequester = focus)
        }
    }
}

private fun DrawScope.drawLiquid(p: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxR = size.minDimension * 0.55f
    val count = 22
    for (i in 0 until count) {
        val startRadius = maxR * (1.1f + (i % 5) * 0.18f)
        val r = startRadius * (1f - p)
        val angle = Math.toRadians((i * 137.5 + p * 540.0))
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        val blob = size.minDimension * (0.05f + (i % 4) * 0.012f) * (1f - 0.4f * p) * 2.4f
        val color = GlowColors[i % GlowColors.size]
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.85f), Color.Transparent),
                center = Offset(x, y),
                radius = blob
            ),
            radius = blob,
            center = Offset(x, y),
            blendMode = BlendMode.Plus
        )
    }
    val core = maxR * 0.5f * p + 1f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.45f * p), Color(0xFF4B7BFF).copy(alpha = 0.25f * p), Color.Transparent),
            center = Offset(cx, cy),
            radius = core
        ),
        radius = core,
        center = Offset(cx, cy),
        blendMode = BlendMode.Plus
    )
}

// ---------------------------------------------------------------------------------------------
// Шаг 1: выбор скина
// ---------------------------------------------------------------------------------------------

@Composable
private fun SkinStep(settings: LauncherSettings, vm: MainViewModel, onNext: () -> Unit) {
    val first = remember { FocusRequester() }
    LaunchedEffect(Unit) { first.requestSafely(300) }

    StepScaffold(
        title = "Выберите стиль",
        subtitle = "Его можно изменить позже в настройках",
        actions = { GlassButton(text = "Далее", onClick = onNext) }
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            SkinCard(
                title = "Vortex",
                subtitle = "Классический экран с доком",
                gaming = false,
                selected = settings.skinType == SkinType.CLASSIC,
                modifier = Modifier.weight(1f),
                focusRequester = first,
                onClick = { vm.setSkin(SkinType.CLASSIC) }
            )
            SkinCard(
                title = "Vortex Gaming",
                subtitle = "Игровой экран в стиле консоли",
                gaming = true,
                selected = settings.skinType == SkinType.GAMING,
                modifier = Modifier.weight(1f),
                focusRequester = null,
                onClick = { vm.setSkin(SkinType.GAMING) }
            )
        }
    }
}

@Composable
private fun SkinCard(
    title: String,
    subtitle: String,
    gaming: Boolean,
    selected: Boolean,
    modifier: Modifier,
    focusRequester: FocusRequester?,
    onClick: () -> Unit
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.05f,
        focusRequester = focusRequester
    ) { focused ->
        Column(
            Modifier
                .liquidGlass(RoundedCornerShape(24.dp), focused || selected)
                .padding(14.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                if (gaming) {
                    AnimatedGradient(StockWallpapers.ps5, Modifier.fillMaxSize(), animated = false)
                    Row(
                        Modifier.align(Alignment.TopStart).padding(start = 14.dp, top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(5) { index ->
                            Box(
                                Modifier
                                    .size(if (index == 0) 34.dp else 26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = if (index == 0) 0.55f else 0.25f))
                            )
                        }
                    }
                } else {
                    AnimatedGradient(StockWallpapers.palettes[0], Modifier.fillMaxSize(), animated = false)
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp)
                            .size(120.dp, 28.dp)
                            .liquidGlass(RoundedCornerShape(8.dp))
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
                if (selected) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Шаг 2: обои
// ---------------------------------------------------------------------------------------------

@Composable
private fun WallpaperStep(settings: LauncherSettings, vm: MainViewModel, onNext: () -> Unit) {
    val first = remember { FocusRequester() }
    LaunchedEffect(Unit) { first.requestSafely(300) }
    val pick = rememberImagePicker { uri -> vm.setWallpaper("uri:$uri") }

    StepScaffold(
        title = "Обои",
        subtitle = "Выберите живой фон или загрузите свою картинку",
        actions = { GlassButton(text = "Далее", onClick = onNext) }
    ) {
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(StockWallpapers.palettes.size) { index ->
                val selected = settings.wallpaper == "stock:$index"
                TvFocusable(
                    onClick = { vm.setWallpaper("stock:$index") },
                    focusScale = 1.1f,
                    focusRequester = if (index == 0) first else null
                ) { focused ->
                    Box(
                        Modifier
                            .size(170.dp, 100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                if (focused || selected) 2.dp else 1.dp,
                                Color.White.copy(alpha = if (focused || selected) 1f else 0.4f),
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        AnimatedGradient(StockWallpapers.palette(index), Modifier.fillMaxSize(), animated = false)
                        Text(
                            StockWallpapers.names[index],
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                        )
                        if (selected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(20.dp)
                            )
                        }
                    }
                }
            }
            items(1) {
                TvFocusable(onClick = pick, focusScale = 1.1f) { focused ->
                    Column(
                        Modifier
                            .size(170.dp, 100.dp)
                            .liquidGlass(RoundedCornerShape(16.dp), focused),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Свои обои", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Шаг 3: «Поставить по умолчанию»
// ---------------------------------------------------------------------------------------------

@Composable
private fun DefaultStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val first = remember { FocusRequester() }
    LaunchedEffect(Unit) { first.requestSafely(300) }
    val roleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onNext()
    }

    StepScaffold(
        title = "Лаунчер по умолчанию",
        subtitle = "Vortex будет открываться при включении ТВ и по кнопке Домой",
        actions = {
            GlassButton(text = "Пропустить", onClick = onNext)
            Spacer(Modifier.width(16.dp))
            GlassButton(
                text = "Поставить по умолчанию",
                icon = Icons.Filled.Home,
                focusRequester = first,
                onClick = {
                    requestDefaultLauncher(
                        context = context,
                        launchRole = { intent -> roleLauncher.launch(intent) },
                        onDone = onNext
                    )
                }
            )
        }
    ) {
        Text(
            "Откроется системный диалог выбора. Выберите Vortex Launcher и подтвердите.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp
        )
    }
}

private fun requestDefaultLauncher(context: Context, launchRole: (Intent) -> Unit, onDone: () -> Unit) {
    try {
        val roleManager = context.getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
            if (roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                onDone()
            } else {
                launchRole(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
            }
            return
        }
    } catch (e: Exception) {
        // падаем в запасные варианты ниже
    }
    val fallbacks = listOf(
        Intent(android.provider.Settings.ACTION_HOME_SETTINGS),
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
    )
    for (intent in fallbacks) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            break
        } catch (e: Exception) {
            // пробуем следующий
        }
    }
    onDone()
}

// ---------------------------------------------------------------------------------------------
// Шаг 4: профиль
// ---------------------------------------------------------------------------------------------

@Composable
private fun ProfileStep(settings: LauncherSettings, vm: MainViewModel) {
    val profiles by vm.profiles.collectAsStateWithLifecycle()
    val first = remember { FocusRequester() }
    LaunchedEffect(profiles.isNotEmpty()) {
        if (profiles.isNotEmpty()) first.requestSafely(300)
    }
    val activeId = settings.activeProfileId.takeIf { id -> profiles.any { it.id == id } }
        ?: profiles.firstOrNull()?.id
    val pickAvatar = rememberImagePicker { uri ->
        activeId?.let { vm.setProfileAvatar(it, uri.toString()) }
    }

    StepScaffold(
        title = "Выберите профиль",
        subtitle = "Без аватарки будет минималистичная чёрно-белая иконка",
        actions = {
            GlassButton(text = "Поставить аватарку", onClick = pickAvatar)
            Spacer(Modifier.width(16.dp))
            GlassButton(
                text = "Готово",
                icon = Icons.Filled.Check,
                onClick = {
                    activeId?.let { vm.selectProfile(it) }
                    vm.completeOnboarding()
                }
            )
        }
    ) {
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(profiles, key = { it.id }) { profile ->
                val selected = profile.id == activeId
                TvFocusable(
                    onClick = { vm.selectProfile(profile.id) },
                    focusScale = 1.12f,
                    focusRequester = if (profile.id == profiles.first().id) first else null
                ) { focused ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ProfileAvatar(profile.avatarUri, 96.dp, highlight = focused || selected)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            profile.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(110.dp)
                        )
                    }
                }
            }
            items(1) {
                TvFocusable(onClick = { vm.addProfile() }, focusScale = 1.12f) { focused ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(96.dp).liquidGlass(androidx.compose.foundation.shape.CircleShape, focused),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Добавить", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/settings/SettingsScreen.kt`

```kotlin
package com.vortex.launcher.ui.settings

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.data.ClockPosition
import com.vortex.launcher.data.LauncherSettings
import com.vortex.launcher.data.SkinType
import com.vortex.launcher.data.db.ProfileEntity
import com.vortex.launcher.data.resolveDock
import com.vortex.launcher.ui.MainViewModel
import com.vortex.launcher.ui.components.AnimatedGradient
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.GlassButton
import com.vortex.launcher.ui.components.ProfileAvatar
import com.vortex.launcher.ui.components.StockWallpapers
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.WallpaperBackground
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.rememberImagePicker
import com.vortex.launcher.ui.components.requestSafely

private enum class Category(val title: String, val icon: ImageVector) {
    SKIN("Скин", Icons.Filled.Palette),
    CLOCK("Часы", Icons.Filled.AccessTime),
    DOCK("Док", Icons.Filled.ViewModule),
    PROFILES("Профили", Icons.Filled.Person),
    WALLPAPER("Обои", Icons.Filled.Image),
    WIDGETS("Виджеты", Icons.Filled.Widgets),
    WEATHER("Погода", Icons.Filled.Cloud)
}

/** Настройки в два столбца: слева категории, справа содержимое. */
@Composable
fun SettingsScreen(
    vm: MainViewModel,
    settings: LauncherSettings,
    apps: List<AppInfo>,
    profiles: List<ProfileEntity>,
    active: Boolean,
    onSkinChange: (SkinType) -> Unit
) {
    var category by remember { mutableStateOf(Category.SKIN) }
    val firstCategory = remember { FocusRequester() }
    LaunchedEffect(active) {
        if (active) firstCategory.requestSafely(150)
    }

    Box(Modifier.fillMaxSize()) {
        WallpaperBackground(settings.wallpaper, animated = false)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.78f)))

        Row(Modifier.fillMaxSize()) {
            Column(
                Modifier.width(250.dp).fillMaxHeight().padding(horizontal = 22.dp, vertical = 30.dp)
            ) {
                Text("Настройки", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(Category.values().toList(), key = { it.name }) { item ->
                        val selected = item == category
                        TvFocusable(
                            onClick = { category = item },
                            modifier = Modifier.fillMaxWidth(),
                            focusScale = 1.04f,
                            focusRequester = if (item == Category.SKIN) firstCategory else null,
                            onFocused = { category = item }
                        ) { focused ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .liquidGlass(RoundedCornerShape(16.dp), focused || selected)
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(item.title, color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            Box(Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.12f)))

            Box(Modifier.weight(1f).fillMaxHeight().padding(28.dp)) {
                Crossfade(targetState = category, label = "settingsPane") { current ->
                    when (current) {
                        Category.SKIN -> SkinPane(settings, onSkinChange)
                        Category.CLOCK -> ClockPane(settings, vm)
                        Category.DOCK -> DockPane(settings, apps, vm)
                        Category.PROFILES -> ProfilesPane(profiles, settings.activeProfileId, vm)
                        Category.WALLPAPER -> WallpaperPane(settings, vm)
                        Category.WIDGETS -> WidgetsPane(settings, vm)
                        Category.WEATHER -> WeatherPane(settings, vm)
                    }
                }
            }
        }
    }
}

// ----------------------------- общие элементы -----------------------------

@Composable
private fun PaneTitle(text: String) {
    Text(text, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun OptionCard(
    title: String,
    subtitle: String?,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TvFocusable(onClick = onClick, modifier = modifier, focusScale = 1.05f) { focused ->
        Row(
            Modifier
                .fillMaxWidth()
                .liquidGlass(RoundedCornerShape(20.dp), focused || selected)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                }
            }
            if (selected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    TvFocusable(onClick = onToggle, modifier = Modifier.fillMaxWidth(), focusScale = 1.02f) { focused ->
        Row(
            Modifier
                .fillMaxWidth()
                .liquidGlass(RoundedCornerShape(18.dp), focused)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .size(46.dp, 26.dp)
                    .clip(CircleShape)
                    .background(if (checked) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.2f)),
                contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    Modifier
                        .padding(3.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (checked) Color.Black else Color.White.copy(alpha = 0.8f))
                )
            }
        }
    }
}

@Composable
private fun Pill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TvFocusable(onClick = onClick, modifier = modifier, focusScale = 1.08f) { focused ->
        Text(
            text,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .liquidGlass(RoundedCornerShape(18.dp), focused)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// ----------------------------- панели -----------------------------

@Composable
private fun SkinPane(settings: LauncherSettings, onSkinChange: (SkinType) -> Unit) {
    Column {
        PaneTitle("Скин")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OptionCard("Vortex", "Классический режим с доком", settings.skinType == SkinType.CLASSIC, Modifier.weight(1f)) {
                onSkinChange(SkinType.CLASSIC)
            }
            OptionCard("Vortex Gaming", "Игровой режим", settings.skinType == SkinType.GAMING, Modifier.weight(1f)) {
                onSkinChange(SkinType.GAMING)
            }
        }
    }
}

@Composable
private fun ClockPane(settings: LauncherSettings, vm: MainViewModel) {
    Column {
        PaneTitle("Положение часов")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OptionCard("Слева", null, settings.clockPosition == ClockPosition.TOP_START, Modifier.weight(1f)) {
                vm.setClockPosition(ClockPosition.TOP_START)
            }
            OptionCard("По центру", null, settings.clockPosition == ClockPosition.TOP_CENTER, Modifier.weight(1f)) {
                vm.setClockPosition(ClockPosition.TOP_CENTER)
            }
            OptionCard("Справа", null, settings.clockPosition == ClockPosition.TOP_END, Modifier.weight(1f)) {
                vm.setClockPosition(ClockPosition.TOP_END)
            }
        }
    }
}

@Composable
private fun DockPane(settings: LauncherSettings, apps: List<AppInfo>, vm: MainViewModel) {
    val dock = remember(settings.dockApps, apps) { resolveDock(settings.dockApps, apps) }
    var slot by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        PaneTitle("Управление доком")
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            dock.forEachIndexed { index, app ->
                TvFocusable(onClick = { slot = index }, focusScale = 1.1f) { focused ->
                    Box(
                        Modifier
                            .size(68.dp)
                            .liquidGlass(RoundedCornerShape(16.dp), focused || slot == index),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(app.packageName, Modifier.size(46.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "Выберите приложение для слота ${slot + 1}",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp
        )
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(96.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            gridItems(apps, key = { it.packageName }) { app ->
                TvFocusable(
                    onClick = {
                        val list = dock.map { it.packageName }.toMutableList()
                        if (slot in list.indices) {
                            val existing = list.indexOf(app.packageName)
                            if (existing >= 0) {
                                list[existing] = list[slot]
                            }
                            list[slot] = app.packageName
                            vm.setDock(list)
                        }
                    },
                    focusScale = 1.08f
                ) { focused ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .liquidGlass(RoundedCornerShape(14.dp), focused)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppIcon(app.packageName, Modifier.size(40.dp))
                        Text(
                            app.label,
                            color = Color.White,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilesPane(profiles: List<ProfileEntity>, activeId: Long, vm: MainViewModel) {
    var avatarTarget by remember { mutableStateOf<Long?>(null) }
    val pick = rememberImagePicker { uri ->
        avatarTarget?.let { vm.setProfileAvatar(it, uri.toString()) }
    }

    Column(Modifier.fillMaxSize()) {
        PaneTitle("Профили")
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(profiles, key = { it.id }) { profile ->
                var name by remember(profile.id) { mutableStateOf(profile.name) }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .liquidGlass(RoundedCornerShape(18.dp), profile.id == activeId)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(profile.avatarUri, 48.dp, highlight = profile.id == activeId)
                    Spacer(Modifier.width(14.dp))
                    BasicTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            vm.renameProfile(profile.id, it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    Pill(if (profile.id == activeId) "Выбран" else "Выбрать", { vm.selectProfile(profile.id) })
                    Spacer(Modifier.width(8.dp))
                    Pill("Аватар", {
                        avatarTarget = profile.id
                        pick()
                    })
                    if (profile.avatarUri != null) {
                        Spacer(Modifier.width(8.dp))
                        Pill("Сбросить", { vm.setProfileAvatar(profile.id, null) })
                    }
                    if (profiles.size > 1) {
                        Spacer(Modifier.width(8.dp))
                        Pill("Удалить", { vm.deleteProfile(profile.id) })
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        GlassButton(text = "Добавить профиль", icon = Icons.Filled.Add, onClick = { vm.addProfile() })
    }
}

@Composable
private fun WallpaperPane(settings: LauncherSettings, vm: MainViewModel) {
    val pick = rememberImagePicker { uri -> vm.setWallpaper("uri:$uri") }
    Column {
        PaneTitle("Обои")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(StockWallpapers.palettes.size) { index ->
                val selected = settings.wallpaper == "stock:$index"
                TvFocusable(onClick = { vm.setWallpaper("stock:$index") }, focusScale = 1.1f) { focused ->
                    Box(
                        Modifier
                            .size(150.dp, 90.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                if (focused || selected) 2.dp else 1.dp,
                                Color.White.copy(alpha = if (focused || selected) 1f else 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        AnimatedGradient(StockWallpapers.palette(index), Modifier.fillMaxSize(), animated = false)
                        Text(
                            StockWallpapers.names[index],
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                        )
                        if (selected) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp)
                            )
                        }
                    }
                }
            }
            items(1) {
                TvFocusable(onClick = pick, focusScale = 1.1f) { focused ->
                    Column(
                        Modifier
                            .size(150.dp, 90.dp)
                            .liquidGlass(RoundedCornerShape(14.dp), focused || settings.wallpaper.startsWith("uri:")),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.height(4.dp))
                        Text("Свои обои", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetsPane(settings: LauncherSettings, vm: MainViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PaneTitle("Виджеты")
        ToggleRow("Часы", settings.showClock) { vm.setShowClock(!settings.showClock) }
        ToggleRow("Погода", settings.showWeather) { vm.setShowWeather(!settings.showWeather) }
    }
}

@Composable
private fun WeatherPane(settings: LauncherSettings, vm: MainViewModel) {
    val context = LocalContext.current
    var city by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PaneTitle("Погода")
        ToggleRow("Показывать погоду", settings.showWeather) { vm.setShowWeather(!settings.showWeather) }
        Text("Текущий город: ${settings.weatherCity}", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .weight(1f)
                    .liquidGlass(RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (city.isEmpty()) {
                    Text("Введите город", color = Color.White.copy(alpha = 0.45f), fontSize = 17.sp)
                }
                BasicTextField(
                    value = city,
                    onValueChange = { city = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.width(12.dp))
            GlassButton(
                text = "Найти",
                onClick = {
                    if (city.isNotBlank()) {
                        vm.setCity(city) { ok ->
                            Toast.makeText(
                                context,
                                if (ok) "Город обновлён" else "Город не найден",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (ok) city = ""
                        }
                    }
                }
            )
        }
    }
}
```

## `app/src/main/java/com/vortex/launcher/ui/theme/Theme.kt`

```kotlin
package com.vortex.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VortexColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF111111),
    onSurface = Color.White
)

@Composable
fun VortexTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = VortexColors, content = content)
}
```

## `app/src/main/java/com/vortex/launcher/ui/transition/ModeTransition.kt`

```kotlin
package com.vortex.launcher.ui.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.SkinType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Классика -> Гейминг: из чёрно-красной дыры вылетает белый джойстик, под ним "Vortex Gaming".
 * Гейминг -> Классика: из чёрной дыры вылетает надпись "Vortex".
 * onMidpoint вызывается, когда экран полностью закрыт: за ним меняется режим. Затем плавное проявление.
 */
@Composable
fun ModeTransitionOverlay(
    target: SkinType,
    onMidpoint: () -> Unit,
    onFinished: () -> Unit
) {
    val hole = remember { Animatable(0f) }
    val objectScale = remember { Animatable(0f) }
    val objectRotation = remember { Animatable(-25f) }
    val textAlpha = remember { Animatable(0f) }
    val overlayAlpha = remember { Animatable(1f) }
    val midpoint by rememberUpdatedState(onMidpoint)
    val finished by rememberUpdatedState(onFinished)

    val spin = rememberInfiniteTransition(label = "spin")
    val angle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "angle"
    )

    LaunchedEffect(target) {
        hole.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        coroutineScope {
            launch { objectScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow)) }
            if (target == SkinType.GAMING) {
                launch { objectRotation.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)) }
            }
            launch {
                delay(450)
                textAlpha.animateTo(1f, tween(500))
            }
        }
        delay(700)
        midpoint()
        overlayAlpha.animateTo(0f, tween(700))
        finished()
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = overlayAlpha.value }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val h = hole.value
            drawRect(Color.Black.copy(alpha = (h * 1.5f).coerceAtMost(1f)))
            val c = center
            val radius = size.maxDimension * 0.75f * h
            if (radius > 1f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        0f to Color.Black,
                        0.45f to Color.Black,
                        0.75f to Color(0xFF5A000F),
                        0.92f to Color(0xFFB3001B).copy(alpha = 0.5f),
                        1f to Color.Transparent,
                        center = c,
                        radius = radius
                    ),
                    radius = radius,
                    center = c
                )
                val ring = radius * 0.62f
                for (k in 0 until 3) {
                    drawArc(
                        color = Color(0xFFFF2A3D).copy(alpha = 0.55f * h),
                        startAngle = angle + k * 120f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(c.x - ring, c.y - ring),
                        size = Size(ring * 2f, ring * 2f),
                        style = Stroke(width = 5f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (target == SkinType.GAMING) {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            val s = objectScale.value
                            scaleX = s
                            scaleY = s
                            rotationZ = objectRotation.value
                            translationY = (1f - s.coerceAtMost(1f)) * 160f
                        }
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Vortex Gaming",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = textAlpha.value
                        translationY = (1f - textAlpha.value) * 40f
                    }
                )
            } else {
                Text(
                    text = "Vortex",
                    color = Color.White,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    modifier = Modifier.graphicsLayer {
                        val s = objectScale.value
                        scaleX = s
                        scaleY = s
                        alpha = textAlpha.value
                    }
                )
            }
        }
    }
}
```

## `app/src/main/res/drawable/banner.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="320dp"
    android:height="180dp"
    android:viewportWidth="320"
    android:viewportHeight="180">
    <path
        android:fillColor="#0B0F24"
        android:pathData="M0,0h320v180h-320z" />
    <path
        android:fillColor="#00000000"
        android:pathData="M160,60 A30,30 0 1,1 130,90"
        android:strokeColor="#FFFFFF"
        android:strokeLineCap="round"
        android:strokeWidth="7" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M160,90m-7,0a7,7 0 1,1 14,0a7,7 0 1,1 -14,0" />
</vector>
```

## `app/src/main/res/drawable/ic_launcher_background.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0B0F24"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```

## `app/src/main/res/drawable/ic_launcher_foreground.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#00000000"
        android:pathData="M54,30 A24,24 0 1,1 30,54"
        android:strokeColor="#FFFFFF"
        android:strokeLineCap="round"
        android:strokeWidth="6" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M54,54m-6,0a6,6 0 1,1 12,0a6,6 0 1,1 -12,0" />
</vector>
```

## `app/src/main/res/mipmap-anydpi/ic_launcher.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

## `app/src/main/res/values/strings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Vortex Launcher</string>
</resources>
```

## `app/src/main/res/values/themes.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Vortex" parent="@android:style/Theme.Material.NoActionBar">
        <item name="android:windowBackground">@android:color/black</item>
        <item name="android:colorBackground">@android:color/black</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>
</resources>
```

## `build.gradle.kts`

```kotlin
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}
```

## `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx3g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official
```

## `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "VortexLauncher"
include(":app")
```