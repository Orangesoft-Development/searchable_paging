package by.orangesoft.paging

import kotlinx.coroutines.Job
import java.lang.StringBuilder

/**
 * Created by set.
 */
abstract class SearchableListRepository<DB, API>  constructor(
    factory: SearchableDataSourceFactory<DB>, parentJob: Job? = null
) : BaseRefreshableRepository<DB, List<API>>(factory, parentJob = parentJob) {

    fun setQueries(query: Pair<String, List<String>>){
        if(!validateQueryKey(query.first))
            return

        val queryString = StringBuilder()
        query.second.forEach {
            if(queryString.isBlank())
                queryString.append(it)
            else
                queryString.append("%' OR ${query.first} LIKE '%$it")
        }

        super.setQuery(query.first to queryString.toString(), false)
    }

    override fun setQuery(query: Pair<String, String?>, refresh: Boolean) {
        if(query.second == null)
            setQueries(query.first to ArrayList())
        else
            setQueries(query.first to listOf(query.second!!))

        if(refresh)
            super.refresh(refresh)
    }

    fun getQueries(key: String): List<String> = super.getQuery(key).split(".%.{1,16}.%").map { it.replace("%","") }.let {
        if(it.size == 1 && it[0].isEmpty())
            ArrayList()
        else
            it
    }
}