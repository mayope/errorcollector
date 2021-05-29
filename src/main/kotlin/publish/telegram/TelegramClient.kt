package de.mayope.errorcollector.publish.telegram

import feign.Feign
import feign.Headers
import feign.Request
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import java.util.concurrent.TimeUnit

interface TelegramClient {

    @Headers("Content-Type: application/json; charset=utf-8")
    @RequestLine("POST /sendMessage")
    fun postException(telegramMessage: TelegramMessage)
}

class TelegramClientBuilder {
    fun build(url: String, connectTimeOut: Long, readTimeOut: Long, botToken: String): TelegramClient {
        return Feign.builder().run {
            encoder(JacksonEncoder())
            decoder(JacksonDecoder())
            options(
                Request.Options(
                    connectTimeOut, TimeUnit.SECONDS, readTimeOut, TimeUnit.SECONDS, true
                )
            )
            target(TelegramClient::class.java, "$url/bot$botToken")
        }
    }
}
