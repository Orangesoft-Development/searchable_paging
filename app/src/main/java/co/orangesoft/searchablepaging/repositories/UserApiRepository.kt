package co.orangesoft.searchablepaging.repositories

import android.util.Log
import co.orangesoft.searchablepaging.api.ApiService
import co.orangesoft.searchablepaging.models.User

@Deprecated("will be removed")
class UserApiRepository(val apiService: ApiService) {

    suspend fun getUsersFromNetwork(per_page: Int? = 100, since: Long? = 0): List<User> {
        Log.e("TAG", "getting users since $since")
        return apiService.getUsers(per_page, since)
    }
}