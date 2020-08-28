package co.orangesoft.searchable_paging.api

import co.orangesoft.searchable_paging.models.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getSearchUsers(@Query("per_page") perPage: Int? = 100,
                               @Query("since") since: Long? = 0,
                               @Query("q") query: String? = null): SearchResult

}