package com.vortex.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ClockState(val time: String, val date: String, val greeting: String)

private val TimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DateFormat: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("ru"))

private fun currentClock(): ClockState {
    val now = LocalDateTime.now()
    val greeting = when (now.hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..22 -> "Добрый вечер"
        else -> "Доброй ночи"
    }
    return ClockState(
        time = now.format(TimeFormat),
        date = now.format(DateFormat).replaceFirstChar { it.uppercase() },
        greeting = greeting
    )
}

/** Обновляется раз в минуту, ровно на границе минуты. */
@Composable
fun rememberClockState(): State<ClockState> = produceState(initialValue = currentClock()) {
    while (true) {
        value = currentClock()
        delay(60_000L - System.currentTimeMillis() % 60_000L + 100L)
    }
}

@Composable
fun ClockBlock(
    clock: ClockState,
    weatherText: String?,
    name: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val greeting = if (!name.isNullOrBlank()) "${clock.greeting}, $name" else clock.greeting
        Text(
            text = greeting,
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = listOfNotNull(clock.date, weatherText).joinToString("  ·  "),
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 16.sp
        )
        Text(
            text = clock.time,
            style = TextStyle(
                fontSize = 140.sp,
                lineHeight = 140.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-4).sp,
                color = Color.White.copy(alpha = 0.38f)
            )
        )
    }
}

/** Мини-островок Liquid Glass в правом верхнем углу: вызывает Центр управления. */
@Composable
fun ControlIsland(
    timeText: String,
    avatarUri: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    TvFocusable(
        onClick = onClick,
        modifier = modifier,
        focusScale = 1.1f,
        focusRequester = focusRequester
    ) { focused ->
        Row(
            modifier = Modifier
                .liquidGlass(RoundedCornerShape(50), focused)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(timeText, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(10.dp))
            ProfileAvatar(avatarUri, 28.dp)
        }
    }
}
