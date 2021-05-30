package net.mayope.errorcollector.issue

import java.net.URLEncoder
import java.util.Locale

internal enum class IssueProvider(val titleField: String, val bodyField: String) {
    JIRA("summary", "description"),
    GITHUB("title", "body");

    companion object {
        fun getMatching(provider: String) =
            values().firstOrNull { it.name.lowercase(Locale.getDefault()) == provider } ?: error(
                "provider: $provider not supported yet"
            )
    }
}

internal class IssueService(
    private val baseUrl: String,
    private val issueProvider: IssueProvider
) {

    fun issueLink(title: String, stacktrace: String, serviceName: String = "", pastebinLink: String? = null): String {
        val encodedTitle = encodeText("$serviceName: $title")
        val encodedBody = body(stacktrace, pastebinLink)
        return if (baseUrl.contains("?")) {
            "$baseUrl&${issueProvider.titleField}=$encodedTitle&${issueProvider.bodyField}=$encodedBody"
        } else {
            "$baseUrl?${issueProvider.titleField}=$encodedTitle&${issueProvider.bodyField}=$encodedBody"
        }
    }

    private fun body(stacktrace: String, pastebinLink: String?): String {
        return if (pastebinLink != null) {
            "Stacktrace: $pastebinLink"
        } else {
            encodeText("Stacktrace: $stacktrace")
        }
    }

    private fun encodeText(text: String) = URLEncoder.encode(text, Charsets.UTF_8)
}
