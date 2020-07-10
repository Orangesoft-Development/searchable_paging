package co.orangesoft.searchablepaging

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.orangesoft.searchablepaging.ui.main.view.MainActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("co.orangesoft.searchablepaging.test", appContext.packageName)
    }

    @Rule
    val testRule: CountingTaskExecutorRule = CountingTaskExecutorRule()

    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), UserList::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity: Activity =
            InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
        testRule.drainTasks(10, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val recyclerView: RecyclerView = activity.findViewById(R.id.cheeseList)
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.getAdapter(), notNullValue())
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.getAdapter().getItemCount() > 0, `is`(true))
    }

    @Throws(InterruptedException::class)
    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync({
            recyclerView.getAdapter().registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
                    }

                    fun onChanged() {
                        latch.countDown()
                    }
                })
        })
        if (recyclerView.getAdapter().getItemCount() > 0) {
            return  //already loaded
        }
        assertThat(latch.await(10, TimeUnit.SECONDS), `is`(true))
    }
}
