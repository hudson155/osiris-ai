package osiris.langfuseTracing

import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import osiris.langfuse.Langfuse
import osiris.tracing.AgentEvent
import osiris.tracing.ChatEvent
import osiris.tracing.Event
import osiris.tracing.Listener
import osiris.tracing.ToolEvent
import osiris.tracing.TraceEvent

/**
 * Enables Langfuse tracing.
 */
public class LangfuseListener(
  private val langfuse: Langfuse,
  private val onCreate: suspend (traceId: Uuid) -> Unit = {},
) : Listener {
  private val batchBuilder: BatchBuilder = BatchBuilder()

  override fun event(event: Event) {
    val end = event.end ?: return
    when (end.details) {
      is AgentEvent.End -> batchBuilder.agentEvent(event)
      is ChatEvent.End -> batchBuilder.chatEvent(event)
      is ToolEvent.End -> batchBuilder.toolEvent(event)
      is TraceEvent.End -> batchBuilder.traceEvent(event)
    }
  }

  override fun flush() {
    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
      val (traceId, batchIngestion) = batchBuilder.build() ?: return@launch
      langfuse.client.request {
        method = HttpMethod.Post
        url("api/public/ingestion")
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        setBody(batchIngestion)
      }
      this@LangfuseListener.onCreate(traceId)
    }
  }
}
