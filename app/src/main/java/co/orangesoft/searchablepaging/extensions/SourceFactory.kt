package co.orangesoft.searchablepaging.extensions

import androidx.paging.DataSource
import by.orangesoft.paging.SearchableDao
import by.orangesoft.paging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User

internal class UserSourceFactory(dao: UserDao): SearchableDataSourceFactory<User>(dao) {
    override fun getDataSource(dao: SearchableDao): DataSource.Factory<Int, User> =
        (dao as UserDao).selectPaged()
}