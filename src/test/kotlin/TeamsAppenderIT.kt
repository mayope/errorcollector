import ch.qos.logback.classic.Logger
import de.mayope.errorcollector.TeamsAppender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.Duration

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
        teamsAppender.webhookUrl =
            "https://outlook.office.com/webhook/2307fa4a-e405-4e5b-a688-ceb95e5c095e@8794e153-c3bd" +
            "-4479-8bea-61aeaf167d5a/IncomingWebhook/efa18f3a47944abb9b5c46d59ca2c4e3/" +
            "d07cb24b-0fa8-40d8-a6c6-ff1bc3715236"
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
