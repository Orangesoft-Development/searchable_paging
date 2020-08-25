package co.orangesoft.searchable_paging

interface DatabaseTransactionCallback {

    fun onSuccess()

    fun onError(exception: Throwable)
}