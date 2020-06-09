package by.orangesoft.paging

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by set.
 */
class OnLoadListener {

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
        GlobalScope.launch(Dispatchers.Main) {
            onError?.invoke(error)
        }
    }

    operator fun invoke(isFinish: Boolean = false) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isFinish)
                onFinish?.invoke()
            else
                onStart?.invoke()
        }
    }
}