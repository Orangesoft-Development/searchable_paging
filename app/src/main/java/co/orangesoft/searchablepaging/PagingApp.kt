package co.orangesoft.searchablepaging

import android.app.Application
import co.orangesoft.searchablepaging.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PagingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PagingApp)
            modules(koinModules)
        }
    }
}