package co.orangesoft.searchablepaging.ui.user_list.presenter

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.room.withTransaction
import co.orangesoft.searchablepaging.extensions.tryCatch
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.AppDatabaseRepository
import co.orangesoft.searchablepaging.repositories.UserApiRepository
import co.orangesoft.searchablepaging.ui.base.BasePresenter
import co.orangesoft.searchablepaging.ui.user_list.view.UserListMvpView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserListPresenter(
    app: Application,
    val userApiRepository: UserApiRepository,
    val appDatabaseRepository: AppDatabaseRepository
) : BasePresenter<UserListMvpView>(app) {

    private var savedTextState: SavedTextState? = null

    val userDiffUtilCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val pagedListLiveData: LiveData<PagedList<User>> by lazy {
        val dataSourceFactory = appDatabaseRepository.userDao().selectPaged()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(PER_PAGE)
            .build()

        LivePagedListBuilder(dataSourceFactory, config)
            .setBoundaryCallback(UsersBoundaryCallback())
            .build()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        savedTextState = SavedTextState()
    }

    override fun onPresenterDestroy() {
        super.onPresenterDestroy()

        savedTextState = null
    }

    fun addNewUser(user: User) {
        launch {
            withContext(Dispatchers.IO) { appDatabaseRepository.userDao().insert(user) }
        }
    }

    private suspend fun insertUsersIntoDatabase(usersFromNetwork: List<User>) {
        /**Write users into database **/
        tryCatch({
            withContext(Dispatchers.IO) {
                appDatabaseRepository.withTransaction {
                    appDatabaseRepository.userDao().setNewUsers(usersFromNetwork)
                }
            }
            viewState?.showMessage("Success writing into database")
        }, {
            viewState?.showMessage("Error during writing into database: ${it.message}")
        })
    }

    companion object {
        const val PER_PAGE = 10
    }

    data class SavedTextState(var text: String? = null, var users: List<String>? = null)

    inner class UsersBoundaryCallback : PagedList.BoundaryCallback<User>() {

        override fun onZeroItemsLoaded() {
            launch {
                val usersFromNetwork = withContext(Dispatchers.IO) { userApiRepository.getUsersFromNetwork(PER_PAGE, 0) }
                insertUsersIntoDatabase(usersFromNetwork)
            }
        }

        override fun onItemAtEndLoaded(itemAtEnd: User) {
            launch {
                val usersFromNetwork = userApiRepository.getUsersFromNetwork(PER_PAGE, itemAtEnd.id.toLong())
                appDatabaseRepository.userDao().insertAll(usersFromNetwork)
            }
        }
    }
}