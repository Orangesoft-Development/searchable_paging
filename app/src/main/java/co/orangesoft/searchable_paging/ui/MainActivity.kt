package co.orangesoft.searchable_paging.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.orangesoft.searchable_paging.R
import co.orangesoft.searchable_paging.api.ApiModuleImpl
import co.orangesoft.searchable_paging.extensions.UserSourceFactory
import co.orangesoft.searchable_paging.extensions.UserSourceFactory.Companion.KEY_AVATAR
import co.orangesoft.searchable_paging.extensions.UserSourceFactory.Companion.KEY_LOGIN
import co.orangesoft.searchable_paging.models.User
import co.orangesoft.searchable_paging.repositories.AppDatabaseRepository
import co.orangesoft.searchable_paging.repositories.UserListRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    private val userDiffUtilCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val userPagedListAdapter by lazy {
        UserPagedListAdapter(
            userDiffUtilCallback
        )
    }

    private val userListRepository by lazy {
        val userDao = AppDatabaseRepository.buildDatabase(this).userDao()
        val apiService = ApiModuleImpl().apiService
        UserListRepository(apiService, UserSourceFactory(userDao), Job())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun getPagedListLiveData(): LiveData<PagedList<User>> {
        return userListRepository.getItems()
    }

    private fun initViews() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userPagedListAdapter
        }
        getPagedListLiveData().observe(this, Observer { userPagedListAdapter.submitList(it) })

        val filterParams: HashMap<String, List<Any>> = hashMapOf()
        filterParams[KEY_LOGIN] = listOf("my")
        filterParams[KEY_AVATAR] = listOf("avatars1")
        userListRepository.setQueries(true, filterParams)
    }
}