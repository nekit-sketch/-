package com.vortex.launcher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.memory.MemoryCache
import coil.request.Options
import com.vortex.launcher.data.AppIconData
import com.vortex.launcher.data.AppIconFetcher
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VortexApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val appContext = applicationContext
        val keyer: Keyer<AppIconData> = object : Keyer<AppIconData> {
            override fun key(data: AppIconData, options: Options): String = "appicon:${data.packageName}"
        }
        val factory: Fetcher.Factory<AppIconData> = AppIconFetcher.Factory(appContext.packageManager)
        return ImageLoader.Builder(appContext)
            .components {
                add(keyer)
                add(factory)
            }
            .memoryCache {
                MemoryCache.Builder(appContext).maxSizePercent(0.15).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(appContext.cacheDir.resolve("coil"))
                    .maxSizeBytes(64L * 1024L * 1024L)
                    .build()
            }
            .crossfade(180)
            .allowRgb565(true)
            .respectCacheHeaders(false)
            .build()
    }
}
