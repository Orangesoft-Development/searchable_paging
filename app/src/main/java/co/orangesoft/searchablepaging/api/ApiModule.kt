package co.orangesoft.searchablepaging.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient

/**
 *  Интерфейс модуля работы с API
 **/
interface ApiModule {
    /** Базовый URL сервера**/
    val apiServer: String
    /** Настроенный сериализатор **/
    val moshi: Moshi
    /** Настроенный HTTP клиент **/
    val httpClient: OkHttpClient
    /** Эндпоинты **/
    val apiService: ApiService
}