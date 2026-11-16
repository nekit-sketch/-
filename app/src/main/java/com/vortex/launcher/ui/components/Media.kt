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
