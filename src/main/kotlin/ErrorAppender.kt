package net.mayope.errorcollector

import ch.qos.logback.classic.spi.ILoggingEvent
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
internal class ErrorAppender(
    private val blackList: List<String> = emptyList(),
    private val errorAggregator: ErrorAggregator
) {

    fun stop() {
        errorAggregator.stop()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
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
