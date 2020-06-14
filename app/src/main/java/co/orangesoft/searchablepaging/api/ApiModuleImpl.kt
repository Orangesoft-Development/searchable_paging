package co.orangesoft.searchablepaging.api

import co.orangesoft.searchablepaging.BuildConfig
import com.squareup.moshi.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ApiModuleImpl : ApiModule {

    /** Moshi адаптер для нулевых полей **/
    private val nullStringAdapter: JsonAdapter<String>  by lazy {
        object : JsonAdapter<String>() {
            @ToJson
            override fun toJson(writer: JsonWriter, value: String?) {
                writer.value(value ?: "")
            }

            @FromJson
            override fun fromJson(reader: JsonReader?): String? = reader?.nextString() ?: ""
        }
    }

    /** Moshi адаптер для парсинга дат **/
    private val dateAdapter: JsonAdapter<Date>  by lazy {
        object : JsonAdapter<Date>() {
            @FromJson
            override fun fromJson(reader: JsonReader?): Date? {
                val dateStr = reader?.nextString()
                return try {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).parse(dateStr)
                } catch (e: ParseException) {
                    null
                } catch (e: IllegalArgumentException) {
                    SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss", Locale.getDefault()).parse(dateStr)
                } catch (e: NullPointerException){
                    null
                }
            }

            @ToJson
            override fun toJson(writer: JsonWriter?, value: Date?) {
                if(value == null)
                    writer?.nullValue()
                else
                    try {
                        writer?.value(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(value))
                    } catch (e: ParseException) {
                        writer?.value("")
                    }
            }
        }
    }

    /** OkHttp интерцептор для логирования запросов **/
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
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

    override val apiServer: String = BASE_URL

    /** Настройка OkHttp клиента**/
    override val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /** Настройка Moshi сериализатора**/
    override val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(nullStringAdapter)
            .add(dateAdapter)
            .build()
    }

    /** Настройка Retrofit'a для работы с нашими эндпоинтами**/
    override val apiService: ApiService by lazy {
        Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(httpClient)
            addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        }.build().create(ApiService::class.java)
    }

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }

}