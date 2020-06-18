package co.orangesoft.searchablepaging.repositories

import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.SearchableListRepository
import co.orangesoft.searchablepaging.SearchParamModel
import co.orangesoft.searchablepaging.api.ApiService
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User
import kotlinx.coroutines.Job

class TestPagingRepository(val apiService: ApiService, factory: SearchableDataSourceFactory<User>, parentJob: Job? = null)
    : SearchableListRepository<User, User>(factory, parentJob = parentJob) {

    private val dao: UserDao by lazy { datasource.dao as UserDao }

    override val PAGE_SIZE = 5

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

    override suspend fun loadData(page: Int, limit: Int, params: List<SearchParamModel>): List<User> {
        //TODO format params in accordance with github rules and get resultQuery string
        var resultQuery: String? = null

        if (params.isNotEmpty()) {
            resultQuery = params[0].key + ":" + params[0].values[0]
        }

        return apiService.getSearchUsers(limit, page.toLong(), resultQuery).items
    }

    override suspend fun onDataLoaded(result: List<User>, dao: SearchableDao, force: Boolean) {
        this.dao.insertAll(result)
    }
}

