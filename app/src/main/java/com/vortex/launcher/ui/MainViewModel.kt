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
