package net.mayope.errorcollector.model

import ch.qos.logback.classic.spi.ILoggingEvent
import java.util.concurrent.atomic.AtomicInteger

internal data class ExceptionContainer(
    val count: AtomicInteger = AtomicInteger(0),
    val event: ILoggingEvent
)
