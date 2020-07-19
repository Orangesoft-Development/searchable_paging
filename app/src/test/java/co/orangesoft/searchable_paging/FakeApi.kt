package co.orangesoft.searchable_paging

import co.orangesoft.searchable_paging.api.ApiService
import co.orangesoft.searchable_paging.models.SearchResult
import co.orangesoft.searchable_paging.models.User
import com.android.example.paging.pagingwithnetwork.reddit.api.RedditApi
import com.android.example.paging.pagingwithnetwork.reddit.vo.RedditPost
import java.io.IOException
import kotlin.math.min

/**
 * implements the RedditApi with controllable requests
 */
class FakeApi : ApiService {

    var failureMsg: String? = null

    fun addUser(user: User) {
        items.add(user)
    }

    private fun findUsers(
        subreddit: String,
        limit: Int,
        after: String? = null
    ): List<User> {
        val subReddit = findSubReddit(subreddit)
        val posts = subReddit.findPosts(limit, after)
        return posts.map { RedditApi.RedditChildrenResponse(it.copy()) }
    }

    override suspend fun getSearchUsers(
        per_page: Int?,
        since: Long?,
        query: String?
    ): SearchResult {
        failureMsg?.let {
            throw IOException(it)
        }

        return SearchResult()

    }

//    override suspend fun getTop(
//        @Path("subreddit") subreddit: String,
//        @Query("limit") limit: Int,
//        @Query("after") after: String?,
//        @Query("before") before: String?
//    ): RedditApi.ListingResponse {
//        failureMsg?.let {
//            throw IOException(it)
//        }
//        val items = findPosts(subreddit, limit)
//        val after = items.lastOrNull()?.data?.name
//        return RedditApi.ListingResponse(
//            RedditApi.ListingData(
//                children = items,
//                after = after,
//                before = null
//            )
//        )
//    }

    private class SubReddit(val items: MutableList<User> = arrayListOf()) {
        fun findPosts(limit: Int, after: String?): List<User> {
            if (after == null) {
                return items.subList(0, min(items.size, limit))
            }
            val index = items.indexOfFirst { it.name == after }
            if (index == -1) {
                return emptyList()
            }
            val startPos = index + 1
            return items.subList(startPos, min(items.size, startPos + limit))
        }
    }
}