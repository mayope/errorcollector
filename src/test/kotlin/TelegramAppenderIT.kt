import ch.qos.logback.classic.Logger
import net.mayope.errorcollector.TelegramAppender
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
internal class TelegramAppenderIT {

    private fun getLogger(classInstance: Class<out Any>) = LoggerFactory.getLogger(classInstance) ?: error(
        "Could not get logger!"
    )

    private val botToken: String = System.getenv("BOTTOKEN")
    private val chatId: String = System.getenv("CHATID")

    @Test
    fun testSend() {
        val testDispatcher = TestCoroutineDispatcher()
        val telegramAppender = TelegramAppender(testDispatcher)
        telegramAppender.botToken = botToken
        telegramAppender.chatId = chatId
        telegramAppender.sendIntervalMinutes = 1
        telegramAppender.serviceName = "testService"
        telegramAppender.start()

        val logger = getLogger(TelegramAppenderIT::class.java) as Logger
        logger.addAppender(telegramAppender)

        val message = (1..1000 + 1).joinToString("") { "a" }
        logger.error(message, IllegalArgumentException("hello team"))
        logger.error(message, IllegalArgumentException("hello team"))
        testDispatcher.advanceTimeBy(Duration.ofMinutes(2).toMillis())
    }

    @Test
    fun testSendPastebin() {
        val testDispatcher = TestCoroutineDispatcher()
        val telegramAppender = TelegramAppender(testDispatcher)
        telegramAppender.botToken = botToken
        telegramAppender.chatId = chatId
        telegramAppender.sendIntervalMinutes = 1
        telegramAppender.serviceName = "testService"
        telegramAppender.urlPastebin = System.getenv("PASTEBIN_URL")
        telegramAppender.pastebinUsername = System.getenv("PASTEBIN_USERNAME")
        telegramAppender.pastebinPassword = System.getenv("PASTEBIN_PASSWORD")
        telegramAppender.start()

        val logger = getLogger(TelegramAppenderIT::class.java) as Logger
        logger.addAppender(telegramAppender)

        val message = (1..1000 + 1).joinToString("") { "a" }
        logger.error(message, IllegalArgumentException("hello team"))
        logger.error(message, IllegalArgumentException("hello team"))
        testDispatcher.advanceTimeBy(Duration.ofMinutes(2).toMillis())
    }

    @Test
    fun `test more than 4 exceptions should be splitted`() {
        val testDispatcher = TestCoroutineDispatcher()
        val telegramAppender = TelegramAppender(testDispatcher)
        telegramAppender.botToken = botToken
        telegramAppender.chatId = chatId
        telegramAppender.sendIntervalMinutes = 1
        telegramAppender.serviceName = "testService"
        telegramAppender.start()

        val logger = getLogger(TelegramAppenderIT::class.java) as Logger
        logger.addAppender(telegramAppender)

        logger.error("message1", IllegalArgumentException("hello team"))
        logger.error("message2", IllegalArgumentException("hello team"))
        logger.error("message3", IllegalArgumentException("hello team"))
        logger.error("message4", IllegalArgumentException("hello team"))
        logger.error("message5", IllegalArgumentException("hello team"))
        testDispatcher.advanceTimeBy(Duration.ofMinutes(2).toMillis())
    }
}
