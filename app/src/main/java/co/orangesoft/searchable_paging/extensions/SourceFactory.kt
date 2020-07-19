package co.orangesoft.searchable_paging.extensions

import androidx.paging.DataSource
import co.orangesoft.searchable_paging.SearchableDao
import co.orangesoft.searchable_paging.SearchableDataSourceFactory
import co.orangesoft.searchable_paging.dao.UserDao
import co.orangesoft.searchable_paging.models.User

internal class UserSourceFactory(dao: UserDao): SearchableDataSourceFactory<User>(dao) {

    companion object {
        const val KEY_LOGIN = "login"
        const val KEY_FOLLOWERS = "followers"
    }

    override fun getDataSource(dao: SearchableDao, params: HashMap<String, List<Any>>): DataSource.Factory<Int, User> {
        var loginQuery = "%"
        params[KEY_LOGIN]?.filterIsInstance<String>()?.forEach {
            loginQuery += if (loginQuery == "%") {
                "$it"
            } else {
                " OR login LIKE %$it"
            }
        }

        return (dao as UserDao).getUsersDataSource(loginQuery)
    }
}