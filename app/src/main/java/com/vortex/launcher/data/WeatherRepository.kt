package com.vortex.launcher.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/** Open-Meteo: бесплатный API без ключа. */
@Singleton
class WeatherRepository @Inject constructor() {

    suspend fun fetch(lat: Double, lon: Double): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val body = httpGet(
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
            )
            val current = JSONObject(body).getJSONObject("current")
            WeatherInfo(
                temp = Math.round(current.getDouble("temperature_2m")).toInt(),
                text = describe(current.getInt("weather_code"))
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun geocode(name: String): GeoResult? = withContext(Dispatchers.IO) {
        try {
            val query = URLEncoder.encode(name, "UTF-8")
            val body = httpGet("https://geocoding-api.open-meteo.com/v1/search?name=$query&count=1&language=ru")
            val results = JSONObject(body).optJSONArray("results")
            if (results == null || results.length() == 0) {
                null
            } else {
                val first = results.getJSONObject(0)
                GeoResult(first.getString("name"), first.getDouble("latitude"), first.getDouble("longitude"))
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun httpGet(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        try {
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun describe(code: Int): String = when (code) {
        0 -> "Ясно"
        1, 2 -> "Малооблачно"
        3 -> "Облачно"
        45, 48 -> "Туман"
        in 51..57 -> "Морось"
        in 61..67 -> "Дождь"
        in 71..77 -> "Снег"
        in 80..82 -> "Ливень"
        85, 86 -> "Снегопад"
        in 95..99 -> "Гроза"
        else -> "Облачно"
    }
}
