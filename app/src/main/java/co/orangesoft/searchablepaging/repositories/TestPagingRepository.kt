package co.orangesoft.searchablepaging.repositories

import by.orangesoft.paging.SearchableDao
import by.orangesoft.paging.SearchableDataSourceFactory
import by.orangesoft.paging.SearchableListRepository
import co.orangesoft.searchablepaging.api.ApiService
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User
import kotlinx.coroutines.Job

class TestPagingRepository(val apiService: ApiService, factory: SearchableDataSourceFactory<User>, parentJob: Job? = null)
    : SearchableListRepository<User, User>(factory, parentJob = parentJob) {

    private val dao: UserDao by lazy { datasource.dao as UserDao }

    override fun validateQueryKey(key: String): Boolean {
//        return when(key){
//            KEY_DEVICE -> true
//            KEY_TYPE -> true
//            else -> false
//        }
        return true
    }

    override suspend fun loadData(page: Int, limit: Int): List<User> {
//        var devices = getQueries(KEY_DEVICE).let {
//            if(it.isEmpty())
//                null
//            else
//                it
//        }
//        var types = getQueries(KEY_TYPE).let {
//            if(it.isEmpty())
//                null
//            else
//                it[0]
//        }

//        return apiService.getFiles(page * limit, limit, devices, types).execute().body()!!

        return apiService.getUsers(limit, page.toLong())
    }

    override suspend fun onDataLoaded(
        result: List<User>,
        dao: SearchableDao,
        force: Boolean
    ) {
        this.dao.insertAll(result)
    }
}

