package co.orangesoft.searchable_paging.repositories

import androidx.paging.PagedList
import co.orangesoft.searchable_paging.BaseRefreshableRepository
import co.orangesoft.searchable_paging.SearchableDao
import co.orangesoft.searchable_paging.SearchableDataSourceFactory
import co.orangesoft.searchable_paging.api.ApiService
import co.orangesoft.searchable_paging.dao.UserDao
import co.orangesoft.searchable_paging.extensions.UserSourceFactory.Companion.KEY_AVATAR
import co.orangesoft.searchable_paging.extensions.UserSourceFactory.Companion.KEY_LOGIN
import co.orangesoft.searchable_paging.models.User
import kotlinx.coroutines.Job
import java.lang.StringBuilder

class UserListRepository(val apiService: ApiService, val factory: SearchableDataSourceFactory<User>, parentJob: Job? = null)
    : BaseRefreshableRepository<User, List<User>>(factory, parentJob = parentJob, PAGE_SIZE = 10) {

    private val dao: UserDao by lazy { datasource.dao as UserDao }

    override fun validateQueryKey(key: String): Boolean {
        return when(key) {
            KEY_LOGIN -> true
            KEY_AVATAR -> true
            else -> false
        }
    }

    override suspend fun loadData(page: Int, limit: Int, params: Map<String, List<Any>>): List<User> {
        var resultQuery: StringBuilder? = null

        if (params.isNotEmpty()) {

            resultQuery = StringBuilder()

            val loginValues = params[KEY_LOGIN]
            if (!loginValues.isNullOrEmpty()) {
                resultQuery.append(loginValues[0])
                    .append(" in:")
                    .append(KEY_LOGIN)
            }
        }

        return apiService.getSearchUsers(limit, page.toLong(), resultQuery?.toString()).items
    }

    override suspend fun onDataLoaded(result: List<User>, dao: SearchableDao, force: Boolean) {
        this.dao.insertAll(result)
    }
}

