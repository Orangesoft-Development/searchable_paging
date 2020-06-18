package co.orangesoft.searchablepaging

import kotlinx.coroutines.Job
import java.lang.StringBuilder

/**
 * Created by set.
 */
abstract class SearchableListRepository<DB, API>  constructor(
    factory: SearchableDataSourceFactory<DB>, parentJob: Job? = null
) : BaseRefreshableRepository<DB, List<API>>(factory, parentJob = parentJob) {

    fun setQueries(query: Pair<String, List<String>>, refresh: Boolean = false) {
        if (!validateQueryKey(query.first)) {
            return
        }

        val queryString = StringBuilder()
        query.second.forEach {
            if (queryString.isBlank()) {
                queryString.append(it)
            } else {
                queryString.append("%' OR ${query.first} LIKE '%$it")
            }
        }

        addSearchParam(query)

        super.setQuery(query.first to queryString.toString(), refresh)
    }

    override fun setQuery(query: Pair<String, String?>, refresh: Boolean) {
        val value = query.second
        if (value == null) {
            setQueries(query.first to ArrayList())
        } else {
            setQueries(query.first to listOf(value))
        }

        if (refresh) {
            super.refresh(refresh)
        }
    }
}