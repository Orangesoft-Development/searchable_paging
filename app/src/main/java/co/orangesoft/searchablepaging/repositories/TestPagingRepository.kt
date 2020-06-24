package co.orangesoft.searchablepaging.repositories

import co.orangesoft.searchablepaging.BaseRefreshableRepository
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.api.ApiService
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User
import kotlinx.coroutines.Job
import java.lang.StringBuilder

class TestPagingRepository(val apiService: ApiService, factory: SearchableDataSourceFactory<User>, parentJob: Job? = null)
    : BaseRefreshableRepository<User, List<User>>(factory, parentJob = parentJob, PAGE_SIZE = 5) {

    private val dao: UserDao by lazy { datasource.dao as UserDao }

    companion object {
        const val KEY_LOGIN = "login"
        const val KEY_FOLLOWERS = "followers"
    }

    override fun validateQueryKey(key: String): Boolean {
        return when(key) {
            KEY_LOGIN -> true
            KEY_FOLLOWERS -> true
            else -> false
        }
    }

    override suspend fun loadData(page: Int, limit: Int, params: HashMap<String, List<Any>>): List<User> {
        var resultQuery: StringBuilder? = null

        if (params.isNotEmpty()) {

            resultQuery = StringBuilder()

            params.entries.forEach {
                resultQuery.append(it.key)
                    .append(":")
                    .append(it.value[0])
            }
        }

        return apiService.getSearchUsers(limit, page.toLong(), resultQuery?.toString()).items
    }

    override suspend fun onDataLoaded(result: List<User>, dao: SearchableDao, force: Boolean) {
        this.dao.insertAll(result)
    }
}

