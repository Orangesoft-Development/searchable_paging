package co.orangesoft.searchable_paging.api

import co.orangesoft.searchable_paging.models.SearchResult
import co.orangesoft.searchable_paging.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getSearchUsers(@Query("per_page") perPage: Int? = 100,
                               @Query("since") since: Long? = 0,
                               @Query("q") query: String? = null): SearchResult


    /** Fake success request */
    @GET("/users")
    suspend fun insertUsers(@Query("items") items: Collection<User>): Response<Void>

    /** Fake failed request */
    @GET("/user")
    suspend fun deleteUsers(@Query("items") items: Collection<User>): Response<Void>

}