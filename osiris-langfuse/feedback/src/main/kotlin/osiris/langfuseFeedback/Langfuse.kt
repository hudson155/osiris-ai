package osiris.langfuseFeedback

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import osiris.langfuse.Langfuse

public suspend fun <T : Any> Langfuse.createFeedback(creator: Score.Creator<T>) {
  client.request {
    // https://api.reference.langfuse.com/#tag/score/post/api/public/scores
    method = HttpMethod.Post
    url("api/public/scores")
    contentType(ContentType.Application.Json)
    accept(ContentType.Application.Json)
    setBody(creator)
  }
}
