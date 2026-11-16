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
