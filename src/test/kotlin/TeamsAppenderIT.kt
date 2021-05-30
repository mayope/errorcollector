import ch.qos.logback.classic.Logger
import net.mayope.errorcollector.TeamsAppender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.Duration

@Disabled
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
internal class TeamsAppenderIT {

    private fun getLogger(classInstance: Class<out Any>) = LoggerFactory.getLogger(classInstance) ?: error(
        "Could not get logger!"
    )

    @Test
    fun testSend() {
        val testDispatcher = TestCoroutineDispatcher()
        val teamsAppender = TeamsAppender(testDispatcher)
        teamsAppender.webhookUrl = System.getenv("WEBHOOK_URL")
        teamsAppender.sendIntervalMinutes = 1
        teamsAppender.start()

        val logger = getLogger(TeamsAppenderIT::class.java) as Logger
        logger.addAppender(teamsAppender)

        val message = (1..1000 + 1).joinToString("") { "a" }
        logger.error(message, IllegalArgumentException("hello team"))
        logger.error(message, IllegalArgumentException("hello team"))
        testDispatcher.advanceTimeBy(Duration.ofMinutes(2).toMillis())
    }
}
