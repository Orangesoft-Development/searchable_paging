package co.orangesoft.searchablepaging.dao

import androidx.paging.DataSource
import androidx.room.*
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.models.User

@Dao
abstract class UserDao: SearchableDao {

    @Query("SELECT * FROM user_items WHERE login LIKE '%' || :loginQuery || '%' ORDER BY id ASC")
    abstract fun getUsersDataSource(loginQuery: String): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(users: List<User>)
}