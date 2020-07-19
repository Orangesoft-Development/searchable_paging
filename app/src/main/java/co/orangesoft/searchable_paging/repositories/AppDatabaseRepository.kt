package co.orangesoft.searchable_paging.repositories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.orangesoft.searchable_paging.dao.UserDao
import co.orangesoft.searchable_paging.models.User

@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabaseRepository : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabaseRepository::class.java, "test_app.db")
                .build()
    }
}