package co.orangesoft.searchable_paging

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * Created by set.
 */
abstract class BaseRefreshableRepository<DB, API>(
    protected var datasource: SearchableDataSourceFactory<DB>,
    protected val parentJob: Job? = null,
    protected open val PAGE_SIZE: Int = DEFAULT_PAGE_SIZE,
    protected open val DISTANCE: Int = DEFAULT_DISTANCE,
    protected open val INITIAL_PAGE: Int = DEFAULT_INITIAL_PAGE,
    protected open var PAGE: Int = DEFAULT_INITIAL_PAGE
): SearchableRepository<DB>, CoroutineScope {

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val DEFAULT_DISTANCE: Int = 5
        const val DEFAULT_INITIAL_PAGE: Int = 0
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Main + SupervisorJob(parentJob) }

    private var loadListener: WeakReference<OnLoadListener>? = null

    private val callback: PagedList.BoundaryCallback<DB> = object : PagedList.BoundaryCallback<DB>() {
        private var isFirstLoad = true

        override fun onZeroItemsLoaded() {
            if (isFirstLoad) {
                isFirstLoad = false
                launch { getItem() }
            }
        }

        override fun onItemAtFrontLoaded(itemAtFront: DB) {
            if (isFirstLoad) {
                launch { getItem(true) }
                isFirstLoad = false
            }
        }

        override fun onItemAtEndLoaded(itemAtEnd: DB) {
            if(PAGE > 0) {
                launch { getItem() }
            }
        }
    }

    val pagedConfig: PagedList.Config by lazy {
        PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(DISTANCE)
                .setEnablePlaceholders(true)
                .build()
    }

    val pagedItems: LiveData<PagedList<DB>> by lazy {
        LivePagedListBuilder(datasource , pagedConfig)
                .setBoundaryCallback(callback)
                .setInitialLoadKey(PAGE)
                .build()
    }

    override fun getItems(): LiveData<PagedList<DB>> = pagedItems

    override fun refresh(force: Boolean) {
        launch { getItem(force) }
    }

    override fun setOnLoadListener(listener: OnLoadListener) {
        loadListener?.apply { clear() }
        loadListener = WeakReference(listener)
    }

    private suspend fun getItem(force: Boolean = false) {

        if (force) {
            PAGE = INITIAL_PAGE
        }

        loadListener?.get()?.invoke(false)

        try {
            val result = loadData(PAGE, PAGE_SIZE, datasource.getQueries())
            onDataLoaded(result, datasource.dao, force)
            PAGE++
            loadListener?.get()?.invoke(true)

        } catch (e: Exception){
            e.printStackTrace()
            loadListener?.get()?.invoke(e)
        }
    }

    override fun getQuery(param: String): List<Any> {
        return datasource.getQuery(param)
    }

    override fun getQueries(): Map<String, List<Any>> {
        return datasource.getQueries()
    }

    override fun setQuery(force: Boolean, param: String, values: List<Any>) {

        if (!validateQueryKey(param)) {
            return
        }

        datasource.setQuery(param, values)
        updateQueries(force)
    }

    override fun clearQueries(force: Boolean) {
        datasource.clearQueries()
        updateQueries(force)
    }

    private fun updateQueries(force: Boolean) {
        datasource.invalidateDataSource()

        PAGE = INITIAL_PAGE

        if (force) {
            refresh(force)
        }
    }

    protected abstract fun validateQueryKey(key: String): Boolean
    protected abstract suspend fun loadData(page: Int, limit: Int, params: Map<String, List<Any>>): API
    protected abstract suspend fun onDataLoaded(result: API, dao: SearchableDao, force: Boolean)
}