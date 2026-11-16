package com.vortex.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vortex.launcher.ui.LauncherRoot
import com.vortex.launcher.ui.MainViewModel
import com.vortex.launcher.ui.theme.VortexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // skinType читается из DataStore внутри MainViewModel и применяется в LauncherRoot
        // до первого кадра: Гейминг-режим сохраняется после выключения ТВ.
        setContent {
            VortexTheme {
                LauncherRoot(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshApps()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Нажатие HOME: закрыть панели и вернуться на главный экран
        viewModel.onHomePressed()
    }
}
