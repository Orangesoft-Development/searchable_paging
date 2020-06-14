package co.orangesoft.searchablepaging.di

import androidx.paging.DataSource
import by.orangesoft.paging.SearchableDao
import by.orangesoft.paging.SearchableDataSourceFactory
import co.orangesoft.searchablepaging.api.ApiModuleImpl
import co.orangesoft.searchablepaging.dao.UserDao
import co.orangesoft.searchablepaging.extensions.UserSourceFactory
import co.orangesoft.searchablepaging.models.User
import co.orangesoft.searchablepaging.repositories.AppDatabaseRepository
import co.orangesoft.searchablepaging.repositories.TestPagingRepository
import co.orangesoft.searchablepaging.repositories.UserApiRepository
import co.orangesoft.searchablepaging.ui.main.presenter.MainPresenter
import co.orangesoft.searchablepaging.ui.main.view.MainActivity
import co.orangesoft.searchablepaging.ui.user_list.presenter.UserListPresenter
import co.orangesoft.searchablepaging.ui.user_list.view.UserListActivity
import kotlinx.coroutines.Job
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.ScopeID
import org.koin.dsl.module

private val apiModule: Module = module {
    single { ApiModuleImpl().apiService }
}

private val databaseModule: Module = module {
    single { AppDatabaseRepository.buildDatabase(androidApplication()) }
    single { AppDatabaseRepository.buildDatabase(androidApplication()).userDao() }
}

val mainModule = module {
    scope(named<MainActivity>()) {
        scoped { UserApiRepository(apiService = get()) }
    }

    viewModel { (scopeId : ScopeID) -> MainPresenter(
        app = androidApplication(),
        userApiRepository = getScope(scopeId).get(),
        appDatabaseRepository = getScope(scopeId).get()
    )}
}

val userListModule = module {
    scope(named<UserListActivity>()) {
        scoped { UserApiRepository(apiService = get()) }
        scoped { TestPagingRepository(apiService = get(), factory = UserSourceFactory(dao = get()), parentJob = Job()) }
    }

    viewModel { (scopeId : ScopeID) -> UserListPresenter(
        app = androidApplication(),
        testPagingRepository = getScope(scopeId).get(),
        appDatabaseRepository = getScope(scopeId).get()
    )}
}

val koinModules: List<Module> = listOf(apiModule, databaseModule, mainModule, userListModule)