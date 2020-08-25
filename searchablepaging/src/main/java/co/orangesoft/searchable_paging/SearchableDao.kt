package co.orangesoft.searchable_paging

/**
 * Created by set.
 */
interface SearchableDao<T> {

    suspend fun updateOrInsert(vararg item: T)

    suspend fun delete(vararg item: T)
}