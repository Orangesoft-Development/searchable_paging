package co.orangesoft.searchablepaging.ui.main.view

import androidx.paging.PagedList
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.ui.base.BaseMvpView

interface MainMvpView: BaseMvpView {
    fun showUserLogins(userLogins: String)
}