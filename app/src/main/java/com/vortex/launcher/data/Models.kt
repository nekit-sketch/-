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
