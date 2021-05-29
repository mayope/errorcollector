package de.mayope.errorcollector.publish.telegram


@Suppress("ConstructorParameterNaming")
data class TelegramMessage(
    val chat_id: String,
    val text: String = "",
    val parse_mode: String = "HTML",
)
