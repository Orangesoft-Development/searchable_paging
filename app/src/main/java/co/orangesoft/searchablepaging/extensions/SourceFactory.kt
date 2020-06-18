package co.orangesoft.searchablepaging.extensions

import androidx.paging.DataSource
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User

internal class UserSourceFactory(dao: UserDao): SearchableDataSourceFactory<User>(dao) {
    override fun getDataSource(dao: SearchableDao): DataSource.Factory<Int, User> =
        (dao as UserDao).selectPaged()
}