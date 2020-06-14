package co.orangesoft.searchablepaging.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

suspend fun <R> tryCatch(
    tryBlock: suspend CoroutineScope.() -> R,
    catchBlock: suspend CoroutineScope.(Throwable) -> R,
    finallyBlock: suspend CoroutineScope.() -> Unit = {},
    handleCancellationExceptionManually: Boolean = false
): R {
    return try {
        coroutineScope { tryBlock() }
    } catch (e: Throwable) {
        if (e !is CancellationException || handleCancellationExceptionManually) {
            coroutineScope { catchBlock(e) }
        } else {
            throw e
        }
    } finally {
        coroutineScope { finallyBlock() }
    }
}