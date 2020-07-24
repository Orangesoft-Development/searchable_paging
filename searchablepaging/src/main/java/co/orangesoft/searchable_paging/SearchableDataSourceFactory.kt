package co.orangesoft.searchable_paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

/**
 * Base abstract class for searchable data source. Create your own source factory class and extend it from
 * SearchableDataSourceFactory to use searchable paging library
 *
 * @param dao - dao, which implement SearchableDao
 */
abstract class SearchableDataSourceFactory<DB>(val dao: SearchableDao) : DataSource.Factory<Int, DB>() {

    private val mutableLiveData: MutableLiveData<DataSource<Int, DB>> = MutableLiveData()
    private var params: HashMap<String, List<Any>> = hashMapOf()

    /**
     * Create filtered datasource
     */
    override fun create(): DataSource<Int, DB> {
        val dataSource = getDataSource(dao, params).create()
        getData().postValue(dataSource)
        return dataSource
    }

    /**
     * Get live data of datasource
     * override to use your own
     */
    open fun getData(): MutableLiveData<DataSource<Int, DB>> {
        return mutableLiveData

    }

    fun invalidateDataSource() {
        getData().value?.invalidate()
    }

    /**
     * Get values of searching parameter
     */
    fun getQuery(param: String): List<Any> {
        return params[param] ?: listOf()
    }

    /**
     * Get map of searching parameters and its values
     */
    fun getQueries(): Map<String, List<Any>> {
        return params
    }

    /**
     * Set searching parameter and it values
     */
    fun setQuery(param: String, values: List<Any>) {
        if (values.isEmpty()) {
            params.remove(param)
        } else {
            params[param] = values
        }
    }

    /**
     * Set map of searching parameters and its values
     */
    fun setQueries(params: HashMap<String, List<Any>>) {
        this.params = params
    }

    /**
     * Clear all parameters
     */
    fun clearQueries() {
        params.clear()
    }

    /**
     * Method for build database queries, apply it into dao and get back filtered datasource
     */
    abstract fun getDataSource(dao: SearchableDao, params: HashMap<String, List<Any>>): DataSource.Factory<Int, DB>
}