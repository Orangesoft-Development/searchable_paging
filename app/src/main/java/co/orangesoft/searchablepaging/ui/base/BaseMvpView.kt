package co.orangesoft.searchablepaging.ui.base

import androidx.annotation.StringRes

interface BaseMvpView {
    fun showProgress()
    fun hideProgress()
    fun showMessage(@StringRes stringResId: Int)
    fun showMessage(message: String)
}