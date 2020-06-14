package co.orangesoft.searchablepaging.ui.main.view

import android.content.Intent
import androidx.lifecycle.Observer
import co.orangesoft.searchablepaging.ui.base.BaseActivity
import co.orangesoft.searchablepaging.ui.user_list.view.UserPagedListAdapter
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import androidx.recyclerview.widget.LinearLayoutManager
import co.orangesoft.searchablepaging.R
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.ui.main.presenter.MainPresenter
import co.orangesoft.searchablepaging.ui.user_list.view.UserListActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<MainMvpView, MainPresenter>(), MainMvpView {

    private val mainPresenter by viewModel<MainPresenter> { parametersOf(currentScope.id) }

    override val presenter: MainPresenter
        get() = mainPresenter

    override val viewState: MainMvpView
        get() = this

    override val layoutRes: Int
        get() = R.layout.activity_main


    override fun initViews() {

        bShowLogins.setOnClickListener {
            presenter.getUsers()
        }

        bOpenUserList.setOnClickListener {
            startActivity(Intent(this@MainActivity, UserListActivity::class.java))
        }
    }

    override fun showUserLogins(userLogins: String) {
        tvUserLogins.text = userLogins
    }
}

