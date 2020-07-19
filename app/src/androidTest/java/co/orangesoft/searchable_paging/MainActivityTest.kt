package co.orangesoft.searchable_paging

import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import co.orangesoft.searchable_paging.ui.MainActivity
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Simply sanity test to ensure that activity launches without any issues and shows some data.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    @Rule
    @JvmField
    val testRule = CountingTaskExecutorRule()

    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = InstrumentationRegistry.getInstrumentation()
            .startActivitySync(intent)
        testRule.drainTasks(10, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val recyclerView: RecyclerView = activity.findViewById(R.id.recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter, CoreMatchers.notNullValue())
        waitForAdapterChange(recyclerView)
        MatcherAssert.assertThat(recyclerView.adapter?.itemCount ?: 0 > 0, CoreMatchers.`is`(true))
    }

    @Throws(InterruptedException::class)
    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation()
            .runOnMainSync {
                recyclerView.adapter?.registerAdapterDataObserver(
                    object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            latch.countDown()
                        }

                        override fun onChanged() {
                            latch.countDown()
                        }
                    })
            }
        if (recyclerView.adapter?.itemCount ?: 0 > 0) {
            return  //already loaded
        }
        MatcherAssert.assertThat(latch.await(10, TimeUnit.SECONDS), CoreMatchers.`is`(true))
    }
}