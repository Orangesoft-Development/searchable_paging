package co.orangesoft.searchablepaging.api

import co.orangesoft.searchablepaging.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiModuleImpl {

    /** OkHttp интерцептор для логирования запросов **/
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    /** Настройка OkHttp клиента**/
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /** Настройка Retrofit'a для работы с нашими эндпоинтами**/
    val apiService: ApiService by lazy {
        Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(httpClient)
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(ApiService::class.java)
    }


    /** OkHttp HEADER интерцептор для кастомных HEADER'ов **/
    private class HeaderInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
            )
        }
    }

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }
}
