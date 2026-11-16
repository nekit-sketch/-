package com.vortex.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vortex.launcher.data.AppInfo
import com.vortex.launcher.ui.components.AppIcon
import com.vortex.launcher.ui.components.TvFocusable
import com.vortex.launcher.ui.components.WallpaperBackground
import com.vortex.launcher.ui.components.liquidGlass
import com.vortex.launcher.ui.components.requestSafely

/** Экран «Все приложения»: LazyVerticalGrid с прямоугольными плитками Liquid Glass. */
@Composable
fun AllAppsScreen(
    apps: List<AppInfo>,
    wallpaper: String,
    active: Boolean,
    onLaunch: (String) -> Unit
) {
    val first = remember { FocusRequester() }
    LaunchedEffect(active, apps.isNotEmpty()) {
        if (active && apps.isNotEmpty()) first.requestSafely(150)
    }

    Box(Modifier.fillMaxSize()) {
        WallpaperBackground(wallpaper, animated = false)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
        Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 32.dp)) {
            Text("Все приложения", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                    AppTile(app, if (index == 0) first else null) { onLaunch(app.packageName) }
                }
            }
        }
    }
}

@Composable
private fun AppTile(app: AppInfo, focusRequester: FocusRequester?, onClick: () -> Unit) {
    TvFocusable(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        focusScale = 1.08f,
        focusRequester = focusRequester
    ) { focused ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .liquidGlass(RoundedCornerShape(16.dp), focused)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppIcon(app.packageName, Modifier.size(52.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
