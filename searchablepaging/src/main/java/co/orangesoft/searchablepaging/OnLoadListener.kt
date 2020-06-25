package co.orangesoft.searchablepaging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

/**
 * Created by set.
 */
class OnLoadListener(private val coroutineContext: CoroutineContext) {

    private var onStart: (() -> Unit)? = null
    private var onFinish: (() -> Unit)? = null
    private var onError: ((Throwable) -> Unit)? = null

    fun onStartLoad(listener: () -> Unit){
        onStart = listener
    }

    fun onFinishLoad(listener: () -> Unit){
        onFinish = listener
    }


    fun onErrorLoad(listener: (Throwable) -> Unit){
        onError = listener
    }

    operator fun invoke(error: Throwable) {
        runBlocking(coroutineContext) {
            launch {
                onError?.invoke(error)
            }
        }
    }

    operator fun invoke(isFinish: Boolean = false) {
        runBlocking(coroutineContext) {
            launch(Dispatchers.Main) {
                if (isFinish) {
                    onFinish?.invoke()
                } else {
                    onStart?.invoke()
                }
            }
        }
    }
}