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
