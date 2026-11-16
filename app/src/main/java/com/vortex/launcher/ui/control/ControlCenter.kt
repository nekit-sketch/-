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
