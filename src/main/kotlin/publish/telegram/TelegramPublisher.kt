package net.mayope.errorcollector.publish.telegram

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import net.mayope.errorcollector.publish.ExceptionPublisher
import net.mayope.errorcollector.publish.PublishableException
import org.apache.commons.lang3.StringUtils

// The Message limit in Telegram is 4096 characters.
// We currently don't calculate how many messages could fit in so we just take 4 messages with maximal 1000 characters
private const val EXCEPTION_CHUNK_SIZE = 4
private const val EXCEPTION_TITLE_LIMIT = 200
private const val EXCEPTION_STACKTRACE_LIMIT = 800

internal class TelegramPublisher(
    private val telegramClient: TelegramClient,
    private val chatId: String,
    private val serviceName: String
) : ExceptionPublisher {

    override fun publishExceptions(exceptions: List<PublishableException>) {
        val count = exceptions.sumOf { it.exception.count.toInt() }
        if (count == 0) {
            return
        }
        val title = "$serviceName: Exception Count: $count"
        exceptions.chunked(EXCEPTION_CHUNK_SIZE).map {
            formatChunk(title, it)
        }.forEach {
            val teamsMessage = TelegramMessage(chat_id = chatId, text = it)
            telegramClient.postException(teamsMessage)
        }
    }

    private fun formatChunk(
        title: String,
        exceptions: List<PublishableException>
    ) =
        "$title\n" + exceptions.joinToString("\n\n") {
            createText(it.exception.event, it.exception.count.toInt(), it.issueLink, it.pastebinLink)
        }

    private fun createText(eventObject: ILoggingEvent, count: Int, issueLink: String?, pastebinLink: String?): String {
        val text = ThrowableProxyUtil.asString(eventObject.throwableProxy)
        return "${ellipse(eventObject)} Count: $count\n ${stacktraceContent(text, pastebinLink)}${
        issueLink(issueLink)
        }"
    }

    private fun ellipse(eventObject: ILoggingEvent) =
        StringUtils.abbreviate(eventObject.formattedMessage, EXCEPTION_TITLE_LIMIT)

    private fun stacktraceContent(text: String, pastebinLink: String?) = pastebinLink?.let {
        "<a href=\"$it\">Stacktrace</a>"
    } ?: "<code>${StringUtils.abbreviate(text, EXCEPTION_STACKTRACE_LIMIT)}</code>"

    private fun issueLink(issueLink: String?) = issueLink?.let {
        "<br><a href=\"$it\">create issue</a>"
    } ?: ""
}
