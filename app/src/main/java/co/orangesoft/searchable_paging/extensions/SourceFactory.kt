package co.orangesoft.searchable_paging.extensions

import androidx.paging.DataSource
import co.orangesoft.searchable_paging.SearchableDao
import co.orangesoft.searchable_paging.SearchableDataSourceFactory
import co.orangesoft.searchable_paging.dao.UserDao
import co.orangesoft.searchable_paging.models.User

internal class UserSourceFactory(dao: UserDao) : SearchableDataSourceFactory<User>(dao) {

    companion object {
        const val KEY_LOGIN = "login"
        const val KEY_AVATAR = "avatar_url"
    }

    override fun getDataSource(
        dao: SearchableDao,
        params: HashMap<String, List<Any>>
    ): DataSource.Factory<Int, User> {
        val loginQuery = params[KEY_LOGIN]?.filterIsInstance<String>()?.get(0) ?: "%"
        val avatarQuery = params[KEY_AVATAR]?.filterIsInstance<String>()?.get(0) ?: "%"

        return (dao as UserDao).getUsersDataSource(loginQuery, avatarQuery)
    }
}