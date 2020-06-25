package co.orangesoft.searchablepaging.dao

import androidx.paging.DataSource
import androidx.room.*
import co.orangesoft.searchablepaging.SearchableDao
import co.orangesoft.searchablepaging.models.User

@Dao
abstract class UserDao: SearchableDao {

    @Transaction
    open suspend fun setNewUsers(users: List<User>) {
        deleteAllUsers()
        insertAll(users)
    }

    @Query("SELECT * FROM user_items WHERE login LIKE '%' || :loginQuery || '%' ORDER BY id ASC")
    abstract fun getUsersDataSource(loginQuery: String): DataSource.Factory<Int, User>

    @Query("SELECT * FROM user_items")
    abstract suspend fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(users: List<User>)

    @Delete
    abstract suspend fun delete(user: User)

    @Query("DELETE FROM user_items WHERE login LIKE :userId")
    abstract suspend fun deleteById(userId: Int)

    @Query("DELETE FROM user_items")
    abstract suspend fun deleteAllUsers()

    @Update
    abstract suspend fun updateUser(vararg users: User)
}