package co.orangesoft.searchablepaging

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
    protected val datasource: SearchableDataSourceFactory<DB>,
    protected val parentJob: Job? = null
): SearchableRepository, CoroutineScope {

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val DEFAULT_DISTANCE: Int = 5
        const val DEFAULT_INITIAL_PAGE: Int = 0
    }

    protected open val PAGE_SIZE: Int = DEFAULT_PAGE_SIZE
    protected open val DISTANCE: Int = DEFAULT_DISTANCE
    protected open val INITIAL_PAGE: Int = DEFAULT_INITIAL_PAGE
    protected open var PAGE: Int = DEFAULT_INITIAL_PAGE

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

    private var loadListener: WeakReference<OnLoadListener>? = null

    private val searchParams: MutableList<SearchParamModel> = arrayListOf()
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
            if(PAGE > 0)
                launch { getItem() }
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
        LivePagedListBuilder(datasource, pagedConfig)
                .setBoundaryCallback(callback)
                .setInitialLoadKey(PAGE)
                .build()
    }

    open fun getItems(): LiveData<PagedList<DB>> = pagedItems

    fun refresh(force: Boolean = true) {
        launch { getItem(force) }
    }

    fun setOnLoadListener(listener: OnLoadListener) {
        loadListener?.apply { clear() }
        loadListener = WeakReference(listener)
    }

    private suspend fun getItem(force: Boolean = false) {

        if (force) {
            PAGE = INITIAL_PAGE
        }

        loadListener?.get()?.invoke(false)

        try {
            val result = loadData(PAGE, PAGE_SIZE, searchParams)
            onDataLoaded(result, datasource.dao, force)
            PAGE++
            loadListener?.get()?.invoke(true)

        } catch (e: Exception){
            e.printStackTrace()
            loadListener?.get()?.invoke(e)
        }
    }

    /*Setting query for DB*/
    override fun setQuery(query: Pair<String, String?>, refresh: Boolean) {
        if (!validateQueryKey(query.first)) {
            return
        }

        datasource.setQuery(query)
        datasource.getData().value?.invalidate()

        PAGE = INITIAL_PAGE

        if (refresh) {
            refresh(false)
        }
    }

    override fun getQuery(): String = datasource.getQuery()
    override fun getQuery(key: String): String = datasource.getQuery(key)

    override fun getSearchParams(): List<SearchParamModel> {
        return searchParams
    }

    override fun addSearchParam(query: Pair<String, List<String>>) {
        searchParams.add(SearchParamModel(query.first, query.second))
    }

    override fun clearSearchParams(shouldRefresh: Boolean) {
        searchParams.clear()

        if (shouldRefresh) {
            refresh(shouldRefresh)
        }
    }

    protected abstract fun validateQueryKey(key: String): Boolean
    protected abstract suspend fun loadData(page: Int, limit: Int, params: List<SearchParamModel>): API
    protected abstract suspend fun onDataLoaded(result: API, dao: SearchableDao, force: Boolean)
}