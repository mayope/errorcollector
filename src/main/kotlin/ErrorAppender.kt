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

    @Suppress("TooGenericExceptionCaught")
    fun append(eventObject: ILoggingEvent?) {
        if (eventObject != null) {
            try {
                registerMessage(eventObject)
            } catch (e: Exception) {
                println("Something went wrong during registerMessage: $e")
                // pass
            }
        }
    }

    private fun registerMessage(eventObject: ILoggingEvent) {

        val logMessage = eventObject.formattedMessage
        if (isBlacklisted(logMessage) || isExceptionClassNameBlacklisted(eventObject)) {
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

    private fun isExceptionClassNameBlacklisted(eventObject: ILoggingEvent): Boolean {
        return if (eventObject.throwableProxy == null) {
            false
        } else {
            isBlacklisted(eventObject.throwableProxy.className)
        }
    }
}
