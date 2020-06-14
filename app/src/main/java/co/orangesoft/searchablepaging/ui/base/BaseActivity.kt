package co.orangesoft.searchablepaging.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.orangesoft.searchablepaging.extensions.showToast

abstract class BaseActivity<VIEW: BaseMvpView, PRESENTER: BasePresenter<VIEW>>: AppCompatActivity(), BaseMvpView {

    abstract val presenter: PRESENTER
    abstract val viewState: VIEW
    abstract val layoutRes: Int

    abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutRes)
        initViews()

        presenter.onAttachView(viewState)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetachView()
    }


    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun showMessage(stringResId: Int) {
        showToast(stringResId)
    }

    override fun showMessage(message: String) {
        showToast(message)
    }
}