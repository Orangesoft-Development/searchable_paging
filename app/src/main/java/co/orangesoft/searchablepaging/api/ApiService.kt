package co.orangesoft.searchablepaging.api

import co.orangesoft.searchablepaging.models.SearchResult
import co.orangesoft.searchablepaging.models.User
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getSearchUsers(@Query("per_page") per_page: Int? = 100,
                               @Query("since") since: Long? = 0,
                               @Query("q") query: String? = null): SearchResult

}