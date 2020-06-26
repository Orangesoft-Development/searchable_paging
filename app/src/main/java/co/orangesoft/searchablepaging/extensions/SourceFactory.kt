package co.orangesoft.searchablepaging.extensions

import androidx.paging.DataSource
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User

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