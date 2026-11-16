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
