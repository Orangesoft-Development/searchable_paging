package by.orangesoft.paging

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
    protected val PAGE_SIZE: Int = DEFAULT_PAGE_SIZE,
    protected val DISTANCE: Int = DEFAULT_DISTANCE,
    protected val INITIAL_PAGE: Int = DEFAULT_INITIAL_PAGE,
    protected var PAGE: Int = DEFAULT_INITIAL_PAGE,
    protected val parentJob: Job? = null
): SearchableRepository, CoroutineScope {

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val DEFAULT_DISTANCE: Int = 5
        const val DEFAULT_INITIAL_PAGE: Int = 0
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + SupervisorJob(parentJob) }

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

        if (force) PAGE = INITIAL_PAGE

         loadListener?.get()?.invoke(false) 

        try {
            val result = loadData(PAGE, PAGE_SIZE)
            onDataLoaded(result, datasource.dao, force)
            PAGE++
            
            loadListener?.get()?.invoke(true)
        } catch (e: Exception){
            e.printStackTrace()
            loadListener?.get()?.invoke(e)
        }
    }

    override fun setQuery(query: Pair<String, String?>, refresh: Boolean) {
        if(!validateQueryKey(query.first))
            return

        datasource.setQuery(query)
        datasource.getData().value?.invalidate()

        PAGE = INITIAL_PAGE

        if(refresh)
            refresh(false)
    }

    override fun getQuery(): String = datasource.getQuery()
    override fun getQuery(key: String): String = datasource.getQuery(key)

    protected abstract fun validateQueryKey(key: String): Boolean
    protected abstract suspend fun loadData(page: Int, limit: Int): API
    protected abstract suspend fun onDataLoaded(result: API, dao: SearchableDao, force: Boolean)

}