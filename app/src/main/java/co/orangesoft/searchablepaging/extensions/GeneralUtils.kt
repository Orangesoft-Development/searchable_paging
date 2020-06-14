package co.orangesoft.searchablepaging.extensions

import android.app.Activity
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.StringRes

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(@StringRes stringResId: Int) {
    Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes stringResId: Int) {
    Toast.makeText(activity, stringResId, Toast.LENGTH_SHORT).show()
}