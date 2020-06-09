package by.orangesoft.paging

/**
 * Created by set.
 */
interface SearchableRepository {

    fun setQuery(query: Pair<String, String?>, refresh: Boolean = false)

    fun getQuery(): String

    fun getQuery(key: String): String
}