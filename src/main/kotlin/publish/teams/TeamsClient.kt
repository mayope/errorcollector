package de.mayope.errorcollector.publish.teams

import feign.Feign
import feign.Headers
import feign.Request
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import java.util.concurrent.TimeUnit

interface TeamsClient {

    @Headers("Content-Type: application/json; charset=utf-8")
    @RequestLine("POST /")
    fun postException(teamsMessage: TeamsMessage)
}

class TeamsClientBuilder {
    fun build(url: String, connectTimeOut: Long, readTimeOut: Long): TeamsClient {
        return Feign.builder().run {
            encoder(JacksonEncoder())
            decoder(JacksonDecoder())
            options(
                Request.Options(
                    connectTimeOut, TimeUnit.SECONDS, readTimeOut, TimeUnit.SECONDS, true
                )
            )
            target(TeamsClient::class.java, url)
        }
    }
}
