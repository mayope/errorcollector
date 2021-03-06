package net.mayope.errorcollector.publish

import net.mayope.errorcollector.model.ExceptionContainer

internal data class PublishableException(
    val exception: ExceptionContainer,
    val issueLink: String? = null,
    val pastebinLink: String? = null
)

internal interface ExceptionPublisher {
    fun publishExceptions(exceptions: List<PublishableException>)
}
