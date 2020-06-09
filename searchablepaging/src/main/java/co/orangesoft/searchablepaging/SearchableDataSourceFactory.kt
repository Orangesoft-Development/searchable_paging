package by.orangesoft.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import java.lang.StringBuilder

/**
 * Created by set.
 */
abstract class SearchableDataSourceFactory<DB>(val dao: SearchableDao) : DataSource.Factory<Int, DB>() {
    private val mutableLiveData: MutableLiveData<DataSource<Int, DB>> = MutableLiveData()
    private var dataSource: DataSource<Int, DB>? = null

    private val queries = HashMap<String, String>()

    fun setQuery(query: Pair<String, String?>) {
        if(query.second.isNullOrBlank())
            queries.remove(query.first)
        else
            queries[query.first] = query.second!!
    }

    fun getQuery(): String {

        val result = StringBuilder()
        queries.map { entry ->
            "(${entry.key} LIKE '%${entry.value}%')"
        }.forEach {
            if(result.isNotBlank())
                result.append(" AND ")

            result.append(it)
        }

        return result.let {
            if(it.isEmpty())
                "1 = 1"
            else
                it.toString()
        }
    }

    fun getQueryKeys(): Set<String> = queries.keys
    fun getQuery(key: String): String = queries[key] ?: ""
    fun getQueries(): Map<String, String> = queries
    fun splitQueries(key: String): List<String> = getQuery(key).split(".%.{1,16}.%").map { it.replace("%","") }.let {
        if(it.size == 1 && it[0].isEmpty())
            ArrayList()
        else
            it
    }

    override fun create(): DataSource<Int, DB> {
        dataSource = getDataSource(dao).create()
        mutableLiveData.postValue(dataSource)
        return dataSource!!
    }

    abstract fun getDataSource(dao: SearchableDao): DataSource.Factory<Int, DB>

    open fun getData(): LiveData<DataSource<Int, DB>> {
        return mutableLiveData
    }
}