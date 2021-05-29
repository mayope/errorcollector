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
    var connectTimeOut: Long = 60
    var readTimeOut: Long = 600
    var urlPastebin: String? = null
    var pastebinUsername: String? = null
    var pastebinPassword: String? = null
    var blacklist: String = ""
    var issueProvider: String? = null
    var issueBaseUrl: String? = null
    var sendIntervalMinutes: Long = 5
    var serviceName: String? = null
    var activateOnEnv: String? = null


    private lateinit var errorAppender: ErrorAppender

    override fun start() {

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
        val exceptionBlacklist = blacklist.split(";").filterNot { it.isBlank() }
        val checkedServiceName = serviceName ?: serviceNameEnv() ?: ""

        errorAppender =
            ErrorAppender(
                TelegramPublisher(telegramClient, chatId, checkedServiceName), exceptionBlacklist, issueService,
                pastebinClient, urlPastebin,
                Duration.ofMinutes(sendIntervalMinutes), dispatcher, checkedServiceName
            )
        super.start()
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
