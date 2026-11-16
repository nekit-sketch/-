package com.vortex.launcher.data

import android.content.pm.PackageManager
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

/** Лениво достаёт иконку приложения через Coil, чтобы не держать все Drawable в памяти. */
class AppIconFetcher(
    private val data: AppIconData,
    private val packageManager: PackageManager
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val drawable = try {
            packageManager.getApplicationIcon(data.packageName)
        } catch (e: Exception) {
            return null
        }
        return DrawableResult(
            drawable = drawable,
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory(private val packageManager: PackageManager) : Fetcher.Factory<AppIconData> {
        override fun create(data: AppIconData, options: Options, imageLoader: ImageLoader): Fetcher =
            AppIconFetcher(data, packageManager)
    }
}
