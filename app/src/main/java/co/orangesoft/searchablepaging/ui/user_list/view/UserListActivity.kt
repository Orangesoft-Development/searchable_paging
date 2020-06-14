package co.orangesoft.searchablepaging.ui.user_list.view

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import co.orangesoft.searchablepaging.R
import co.orangesoft.searchablepaging.ui.base.BaseActivity
import co.orangesoft.searchablepaging.ui.user_list.presenter.UserListPresenter
import kotlinx.android.synthetic.main.activity_user_list.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserListActivity : BaseActivity<UserListMvpView, UserListPresenter>(), UserListMvpView {

    private val userListPresenter by viewModel<UserListPresenter> { parametersOf(currentScope.id) }

    override val presenter: UserListPresenter
        get() = userListPresenter

    override val viewState: UserListMvpView
        get() = this

    override val layoutRes: Int
        get() = R.layout.activity_user_list

    private val userPagedListAdapter by lazy { UserPagedListAdapter(presenter.userDiffUtilCallback) }


    override fun initViews() {

        floatingActionButton.setOnClickListener {
            presenter.getPagedListLiveData().value?.let {
                val newUser = it[0]?.copy(id = 2, login = "NEW TEST USER")
                newUser?.let { user -> presenter.addNewUser(user) }
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity)
            adapter = userPagedListAdapter
        }
        presenter.getPagedListLiveData().observe(this, Observer { userPagedListAdapter.submitList(it) })
    }
}