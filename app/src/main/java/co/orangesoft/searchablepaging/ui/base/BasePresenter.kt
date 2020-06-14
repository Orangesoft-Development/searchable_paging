package co.orangesoft.searchablepaging.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BasePresenter <VIEW : BaseMvpView> (app: Application): AndroidViewModel(app), CoroutineScope {

    protected var viewState: VIEW? = null
    private var isFirstLaunch = true
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + CoroutineExceptionHandler { _, exception ->
            Log.e("EXCEPTION", "$exception handled!")
            viewState?.showMessage(exception.message ?: "Something went wrong")
        }

    open fun onFirstViewAttach() {
        Log.e("PRESENTER", "onFirstViewAttach")
    }

    open fun onAttachView(view: VIEW) {
        Log.e("PRESENTER", "attachView")

        viewState = view

        if (isFirstLaunch) {
            isFirstLaunch = false
            onFirstViewAttach()
        }
    }

    open fun onDetachView() {
        Log.e("PRESENTER", "detachView")
        viewState = null
    }

    open fun onPresenterDestroy() {
        Log.e("PRESENTER", "onPresenterDestroy")
        viewState = null
        job.cancel()
    }

    override fun onCleared() {
        onPresenterDestroy()
        super.onCleared()
    }
}