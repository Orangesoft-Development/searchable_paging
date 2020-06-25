package co.orangesoft.searchablepaging.ui.main.presenter

import android.app.Application
import androidx.room.withTransaction
import co.orangesoft.searchablepaging.extensions.tryCatch
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.AppDatabaseRepository
import co.orangesoft.searchablepaging.repositories.UserApiRepository
import co.orangesoft.searchablepaging.ui.base.BasePresenter
import co.orangesoft.searchablepaging.ui.main.view.MainMvpView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPresenter(
    app: Application,
    val userApiRepository: UserApiRepository,
    val appDatabaseRepository: AppDatabaseRepository
) : BasePresenter<MainMvpView>(app) {

    private var savedTextState: SavedTextState? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        savedTextState = SavedTextState()
    }

    override fun onAttachView(view: MainMvpView) {
        super.onAttachView(view)

        savedTextState?.run {
            userLogins?.let { viewState?.showUserLogins(it) }
        }
    }

    override fun onDetachView() {
        super.onDetachView()
    }

    override fun onPresenterDestroy() {
        super.onPresenterDestroy()

        savedTextState = null
    }

    fun getUsers() {
        launch {

            val users = tryCatch({
                val usersFromNetwork = withContext(Dispatchers.IO) { userApiRepository.getUsersFromNetwork(per_page = 10, since = 0) }
                insertUsersIntoDatabase(usersFromNetwork)
                usersFromNetwork

            }, {
                viewState?.showMessage("Error getting users from network: ${it.message}")

                /**Get users from database **/
                withContext(Dispatchers.IO) { appDatabaseRepository.userDao().getAll() }
            })

            val userLogins = getUserLoginsAsString(users)
            savedTextState?.userLogins = userLogins
            viewState?.showUserLogins(userLogins)
        }
    }

    private suspend fun insertUsersIntoDatabase(usersFromNetwork: List<User>) {
        /**Write users into database **/
        tryCatch({
            withContext(Dispatchers.IO) {
                appDatabaseRepository.withTransaction {
                    appDatabaseRepository.userDao().insertAll(usersFromNetwork)
                }
            }
            viewState?.showMessage("Success writing into database")
        }, {
            viewState?.showMessage("Error during writing into database: ${it.message}")
        })
    }

    private fun getUserLoginsAsString(users: List<User>): String {
        var resultText = ""

        users.forEach {
            resultText += "${it.id}. ${it.login} \n"
        }

        return resultText
    }

    data class SavedTextState(var text: String? = null, var userLogins: String? = null)
}