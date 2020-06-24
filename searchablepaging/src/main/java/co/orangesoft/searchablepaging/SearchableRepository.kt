package co.orangesoft.searchablepaging

/**
 * Created by set.
 */
interface SearchableRepository {

    fun getQuery(param: String): List<Any>

    fun getQueries(): Map<String, List<Any>>

    fun setQuery(force: Boolean, param: String, values: List<Any>)

    fun clearParams(force: Boolean = false)
}