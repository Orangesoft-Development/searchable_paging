package co.orangesoft.searchablepaging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

/**
 * Created by set.
 */
abstract class SearchableDataSourceFactory<DB>(val dao: SearchableDao) : DataSource.Factory<Int, DB>() {

    private val mutableLiveData: MutableLiveData<DataSource<Int, DB>> = MutableLiveData()
    private var params: HashMap<String, List<Any>> = hashMapOf()

    override fun create(): DataSource<Int, DB> {
        val dataSource = getDataSource(dao, params).create()
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    open fun getData(): LiveData<DataSource<Int, DB>> {
        return mutableLiveData
    }

    fun invalidateDataSource() {
        getData().value?.invalidate()
    }

    fun getQuery(param: String): List<Any> {
        return params[param] ?: listOf()
    }

    fun getQueries(): Map<String, List<Any>> {
        return params
    }

    fun setQuery(param: String, values: List<Any>) {
        if (values.isEmpty()) {
            params.remove(param)
        } else {
            params[param] = values
        }
    }

    fun setQueries(params: HashMap<String, List<Any>>) {
        this.params = params
    }

    fun clearQueries() {
        params.clear()
    }

    abstract fun getDataSource(dao: SearchableDao, params: HashMap<String, List<Any>>): DataSource.Factory<Int, DB>
}