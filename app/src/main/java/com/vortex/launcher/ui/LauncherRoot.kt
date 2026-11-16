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
