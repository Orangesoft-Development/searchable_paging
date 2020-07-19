package co.orangesoft.searchable_paging.dao

import androidx.paging.DataSource
import androidx.room.*
import co.orangesoft.searchable_paging.SearchableDao
import co.orangesoft.searchable_paging.models.User

@Dao
abstract class UserDao: SearchableDao {

    @Query("SELECT * FROM user_items WHERE login LIKE '%' || :loginQuery || '%' ORDER BY id ASC")
    abstract fun getUsersDataSource(loginQuery: String): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(users: List<User>)
}