package co.orangesoft.searchablepaging.extensions

import androidx.paging.DataSource
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.TestPagingRepository.Companion.KEY_LOGIN

internal class UserSourceFactory(dao: UserDao): SearchableDataSourceFactory<User>(dao) {

    override fun getDataSource(dao: SearchableDao, params: HashMap<String, List<Any>>): DataSource.Factory<Int, User> {
        var loginQuery = "%"
        params[KEY_LOGIN]?.filterIsInstance<String>()?.forEach {
            loginQuery += if (loginQuery == "%") {
                "$it"
            } else {
                " OR login LIKE % $it"
            }
        }

        return (dao as UserDao).getUsersDataSource(loginQuery)
    }
}