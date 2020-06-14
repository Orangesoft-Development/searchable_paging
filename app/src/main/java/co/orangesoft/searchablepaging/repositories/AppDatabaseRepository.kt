package co.orangesoft.searchablepaging.repositories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.models.User

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