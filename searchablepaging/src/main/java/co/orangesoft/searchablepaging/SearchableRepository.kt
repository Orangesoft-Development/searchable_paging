package co.orangesoft.searchablepaging

/**
 * Created by set.
 */
interface SearchableRepository {

    fun setQuery(query: Pair<String, String?>, refresh: Boolean = false)

    fun getQuery(): String

    fun getQuery(key: String): String

    fun getSearchParams(): List<SearchParamModel>

    fun addSearchParam(query: Pair<String, List<String>>)

    fun clearSearchParams(shouldRefresh: Boolean = false)
}