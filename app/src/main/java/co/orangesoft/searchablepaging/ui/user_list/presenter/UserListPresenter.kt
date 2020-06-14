package co.orangesoft.searchablepaging.ui.user_list.presenter

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.AppDatabaseRepository
import co.orangesoft.searchablepaging.repositories.TestPagingRepository
import co.orangesoft.searchablepaging.ui.base.BasePresenter
import co.orangesoft.searchablepaging.ui.user_list.view.UserListMvpView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserListPresenter(
    app: Application,
    val testPagingRepository: TestPagingRepository,
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

    fun getPagedListLiveData(): LiveData<PagedList<User>> {
        return testPagingRepository.pagedItems
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

    data class SavedTextState(var text: String? = null, var users: List<String>? = null)
}