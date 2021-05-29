package de.mayope.errorcollector

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import de.mayope.errorcollector.issue.IssueProvider
import de.mayope.errorcollector.issue.IssueService
import de.mayope.errorcollector.pastebin.PastebinClientBuilder
import de.mayope.errorcollector.publish.teams.TeamsClientBuilder
import de.mayope.errorcollector.publish.teams.TeamsPublisher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.time.Duration

@ObsoleteCoroutinesApi
open class TeamsAppender(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    AppenderBase<ILoggingEvent>() {

    var webhookUrl: String = "https://api.telegram.org"
    var connectTimeOut: Long = 60
    var readTimeOut: Long = 600
    var urlPastebin: String? = null
    var pastebinUsername: String? = null
    var pastebinPassword: String? = null
    var blacklist: String = ""
    var issueProvider: String? = null
    var issueBaseUrl: String? = null
    var sendIntervalMinutes: Long = 5
    var serviceName: String? = null
    var activateOnEnv: String? = null


    private lateinit var errorAppender: ErrorAppender

    override fun start() {
        val issueService = issueBaseUrl?.let {
            IssueService(
                it, IssueProvider.getMatching(
                    issueProvider ?: error("you need to specify an issueProvider if you provide an issueBaseUrl")
                )
            )
        }
        val teamsClient = TeamsClientBuilder().build(webhookUrl, connectTimeOut, readTimeOut)
        val pastebinClient = urlPastebin?.let {
            PastebinClientBuilder().build(it, connectTimeOut, readTimeOut, pastebinUsername, pastebinPassword)
        }
        val exceptionBlacklist = blacklist.split(";").filterNot { it.isBlank() }
        val checkedServiceName = serviceName ?: serviceNameEnv() ?: ""
        errorAppender =
            ErrorAppender(
                TeamsPublisher(teamsClient, checkedServiceName), exceptionBlacklist, issueService, pastebinClient,
                urlPastebin,
                Duration.ofMinutes(sendIntervalMinutes), dispatcher, checkedServiceName
            )
        super.start()
    }

    override fun stop() {
        errorAppender.stop()
    }

    @Suppress("TooGenericExceptionCaught")
    override fun append(eventObject: ILoggingEvent?) {
        if (activeOnEnvPresent(activateOnEnv)) {
            errorAppender.append(eventObject)
        }
    }
}

internal fun serviceNameEnv() = System.getenv(ENV_SERVICE_NAME)
internal fun activeOnEnvPresent(envName: String?) = envName == null || System.getenv(envName) != null
