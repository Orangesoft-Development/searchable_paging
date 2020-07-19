package co.orangesoft.searchable_paging

import co.orangesoft.searchable_paging.models.User
import com.android.example.paging.pagingwithnetwork.reddit.vo.RedditPost
import java.util.concurrent.atomic.AtomicInteger

class UserModelFactory {
    private val counter = AtomicInteger(0)
    fun createUser(subredditName : String) : User {
        val id = counter.incrementAndGet()
        val post = User(
            name = "name_$id",
            title = "title $id",
            score = 10,
            author = "author $id",
            num_comments = 0,
            created = System.currentTimeMillis(),
            thumbnail = null,
            subreddit = subredditName,
            url = null
        )
        post.indexInResponse = -1
        return post
    }
}