package net.mayope.errorcollector.publish.teams

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import net.mayope.errorcollector.publish.ExceptionPublisher
import net.mayope.errorcollector.publish.PublishableException

private const val MAX_MESSAGE_LENGTH = 20500

internal class TeamsPublisher(private val teamsClient: TeamsClient, private val serviceName: String) :
    ExceptionPublisher {

    override fun publishExceptions(exceptions: List<PublishableException>) {
        val count = exceptions.sumOf { it.exception.count.toInt() }
        if (count == 0) {
            return
        }
        val text = exceptions.joinToString("<br><br>") {
            createText(it.exception.event, it.exception.count.toInt(), it.issueLink, it.pastebinLink)
        }.take(MAX_MESSAGE_LENGTH)

        val title = "$serviceName: Exception Count: $count"
        val teamsMessage = TeamsMessage(title = title, text = text)
        teamsClient.postException(teamsMessage)
    }

    /**
     * https://docs.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/connectors-using
     */
    private fun createText(event: ILoggingEvent, count: Int, issueLink: String?, pastebinLink: String?): String {
        val text = ThrowableProxyUtil.asString(event.throwableProxy)
        return "${event.formattedMessage} Count: $count<br>${issueLink(issueLink)}<br>${stacktrace(text, pastebinLink)}"
    }

    private fun stacktrace(text: String, pastebinLink: String?): String? {
        return pastebinLink?.let {
            " <a href=\"$it\">Stacktrace</a>"
        } ?: text
    }

    private fun issueLink(issueLink: String?) =
        issueLink?.let {
            "<a href=\"$it\">create ticket</a>"
        } ?: ""
}
