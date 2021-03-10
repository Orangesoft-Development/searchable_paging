package co.orangesoft.paging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class OnLoadListener(private val coroutineContext: CoroutineContext = Dispatchers.Main) {

    private var onStart: ((currentPage: Int) -> Unit)? = null
    private var onFinish: ((currentPage: Int, loadedItemsSize: Int) -> Unit)? = null
    private var onError: ((Throwable) -> Unit)? = null

    fun onStartLoad(listener: (currentPage: Int) -> Unit) {
        onStart = listener
    }

    fun onFinishLoad(listener: (currentPage: Int, count: Int) -> Unit) {
        onFinish = listener
    }


    fun onErrorLoad(listener: (Throwable) -> Unit) {
        onError = listener
    }

    operator fun invoke(error: Throwable) {
        runBlocking(coroutineContext) {
            onError?.invoke(error)
        }
    }

    operator fun invoke(currentPage: Int) {
        runBlocking(coroutineContext) {
            onStart?.invoke(currentPage)
        }
    }

    operator fun invoke(currentPage: Int, count: Int) {
        runBlocking(coroutineContext) {
            onFinish?.invoke(currentPage, count)
        }
    }
}