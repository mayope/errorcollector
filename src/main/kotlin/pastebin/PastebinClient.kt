package net.mayope.errorcollector.pastebin

import feign.Body
import feign.Feign
import feign.Headers
import feign.Param
import feign.Request
import feign.RequestLine
import feign.Response
import feign.auth.BasicAuthRequestInterceptor
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import java.util.concurrent.TimeUnit

internal interface PastebinClient {

    @Headers("Content-Type: application/octet-stream; charset=utf-8")
    @RequestLine("POST /")
    @Body("{body}")
    fun post(@Param("body") body: String): Response
}

internal fun PastebinClient.uploadText(text: String): String =
    post(text).body().asReader(Charsets.UTF_8).readText().let {
        it.split("/").last()
    }

internal class PastebinClientBuilder {
    fun build(
        url: String,
        connectTimeOut: Long,
        readTimeOut: Long,
        userName: String?,
        password: String?
    ): PastebinClient {
        return Feign.builder().run {
            encoder(JacksonEncoder())
            decoder(JacksonDecoder())
            if (userName != null && password != null) {
                requestInterceptor(BasicAuthRequestInterceptor(userName, password))
            }
            options(
                Request.Options(
                    connectTimeOut, TimeUnit.SECONDS, readTimeOut, TimeUnit.SECONDS, true
                )
            )
            target(PastebinClient::class.java, url)
        }
    }
}
