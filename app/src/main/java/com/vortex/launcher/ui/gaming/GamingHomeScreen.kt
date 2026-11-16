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
