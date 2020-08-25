package co.orangesoft.searchable_paging.dao

import androidx.paging.DataSource
import androidx.room.*
import co.orangesoft.searchable_paging.SearchableDao
import co.orangesoft.searchable_paging.models.User

@Dao
abstract class UserDao: SearchableDao<User> {

    @Query("SELECT * FROM user_items WHERE login LIKE '%' || :loginQuery || '%' AND avatar_url LIKE '%' || :avatarQuery || '%' ORDER BY id ASC")
    abstract fun getUsersDataSource(loginQuery: String, avatarQuery: String): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun updateOrInsert(vararg item: User)

    @Delete
    abstract override suspend fun delete(vararg item: User)
}