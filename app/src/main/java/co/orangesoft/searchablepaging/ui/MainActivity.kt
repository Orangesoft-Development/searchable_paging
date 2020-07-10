package co.orangesoft.searchablepaging.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.orangesoft.searchablepaging.R
import co.orangesoft.searchablepaging.api.ApiModuleImpl
import co.orangesoft.searchablepaging.extensions.UserSourceFactory
import co.orangesoft.searchablepaging.extensions.UserSourceFactory.Companion.KEY_LOGIN
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.AppDatabaseRepository
import co.orangesoft.searchablepaging.repositories.TestPagingRepository
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

    private val testPagingRepository by lazy {
        val userDao = AppDatabaseRepository.buildDatabase(this).userDao()
        val apiService = ApiModuleImpl().apiService
        TestPagingRepository(apiService, UserSourceFactory(userDao), Job())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun getPagedListLiveData(): LiveData<PagedList<User>> {
        return testPagingRepository.pagedItems
    }

    private fun initViews() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userPagedListAdapter
        }
        getPagedListLiveData().observe(this, Observer { userPagedListAdapter.submitList(it) })

        testPagingRepository.setQuery(true, KEY_LOGIN, listOf("my"))
    }
}