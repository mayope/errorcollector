package de.mayope.errorcollector

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import de.mayope.errorcollector.issue.IssueProvider
import de.mayope.errorcollector.issue.IssueService
import de.mayope.errorcollector.pastebin.PastebinClientBuilder
import de.mayope.errorcollector.publish.telegram.TelegramClientBuilder
import de.mayope.errorcollector.publish.telegram.TelegramPublisher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.time.Duration

@ObsoleteCoroutinesApi
open class TelegramAppender(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    AppenderBase<ILoggingEvent>() {

    var url: String = "https://api.telegram.org"
    var chatId: String = ""
    var botToken: String = ""
    var connectTimeOut: Long = CONNECT_TIMEOUT
    var readTimeOut: Long = READ_TIMEOUT
    var urlPastebin: String? = null
    var pastebinUsername: String? = null
    var pastebinPassword: String? = null
    var blacklist: String = ""
    var issueProvider: String? = null
    var issueBaseUrl: String? = null
    var sendIntervalMinutes: Long = SEND_INTERVAL_MINUTES
    var serviceName: String? = null
    var activateOnEnv: String? = null


    private lateinit var errorAppender: ErrorAppender

    override fun start() {

        errorAppender =
            ErrorAppender(
                parseBlacklist(blacklist), buildAggregator()
            )
        super.start()
    }

    private fun buildAggregator(): ErrorAggregator {
        val issueService = issueBaseUrl?.let {
            IssueService(
                it, IssueProvider.getMatching(
                    issueProvider ?: error("you need to specify an issueProvider if you provide an issueBaseUrl")
                )
            )
        }
        val telegramClient = TelegramClientBuilder().build(url, connectTimeOut, readTimeOut, botToken)
        val pastebinClient = urlPastebin?.let {
            PastebinClientBuilder().build(it, connectTimeOut, readTimeOut, pastebinUsername, pastebinPassword)
        }
        val checkedServiceName = serviceName ?: serviceNameEnv() ?: ""
        return ErrorAggregator(
            TelegramPublisher(telegramClient, chatId, checkedServiceName), issueService = issueService,
            pastebinClient = pastebinClient, urlPastebin = urlPastebin,
            sendInterval = Duration.ofMinutes(sendIntervalMinutes), defaultDispatcher = dispatcher,
            serviceName = checkedServiceName
        )
    }

    override fun stop() {
        if (activeOnEnvPresent(activateOnEnv)) {
            errorAppender.stop()
        }
        super.stop()
    }

    @Suppress("TooGenericExceptionCaught")
    override fun append(eventObject: ILoggingEvent?) {
        errorAppender.append(eventObject)
    }
}
