package co.orangesoft.searchablepaging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import java.lang.StringBuilder

/**
 * Created by set.
 */
abstract class SearchableDataSourceFactory<DB>(val dao: SearchableDao) : DataSource.Factory<Int, DB>() {

    private val mutableLiveData: MutableLiveData<DataSource<Int, DB>> = MutableLiveData()

    override fun create(): DataSource<Int, DB> {
        val dataSource = getDataSource(dao).create()
        mutableLiveData.postValue(dataSource)
        return dataSource
    }

    abstract fun getDataSource(dao: SearchableDao): DataSource.Factory<Int, DB>

    open fun getData(): LiveData<DataSource<Int, DB>> {
        return mutableLiveData
    }

    fun invalidateDataSource() {
        getData().value?.invalidate()
    }
}