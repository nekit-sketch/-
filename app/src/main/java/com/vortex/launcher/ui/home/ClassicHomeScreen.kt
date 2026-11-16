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
