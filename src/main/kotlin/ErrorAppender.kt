package de.mayope.errorcollector

import ch.qos.logback.classic.spi.ILoggingEvent
import de.mayope.errorcollector.issue.IssueService
import de.mayope.errorcollector.pastebin.PastebinClient
import de.mayope.errorcollector.publish.ExceptionPublisher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.time.Duration

/**
 * Error collector
 */
@ObsoleteCoroutinesApi
internal class ErrorAppender(
    publisher: ExceptionPublisher,
    private val blackList: List<String> = emptyList(),
    issueService: IssueService? = null,
    pastebinClient: PastebinClient? = null,
    urlPastebin: String?,
    sendInterval: Duration,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    serviceName: String) {

    private val errorAggregator = ErrorAggregator(
        publisher, issueService = issueService, pastebinClient = pastebinClient, urlPastebin = urlPastebin,
        sendInterval = sendInterval, defaultDispatcher = dispatcher, serviceName = serviceName
    )


    fun stop() {
        errorAggregator.stop()
    }

    @Suppress("TooGenericExceptionCaught")
    fun append(eventObject: ILoggingEvent?) {
        if (eventObject != null) {
            try {
                registerMessage(eventObject)
            } catch (e: Exception) {
                // pass
            }
        }
    }

    private fun registerMessage(eventObject: ILoggingEvent) {

        val logMessage = eventObject.formattedMessage
        if (isBlacklisted(logMessage)) {
            return
        }
        errorAggregator.registerMessage(eventObject)
    }


    private fun isBlacklisted(logMessage: String): Boolean {
        blackList.forEach {
            if (logMessage.contains(it)) {
                return true
            }
        }
        return false
    }
}

