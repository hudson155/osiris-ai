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
import osiris.tracing.Event
import osiris.tracing.Listener

/**
 * Enables Langfuse tracing.
 */
public class LangfuseListener(
  private val langfuse: Langfuse,
  private val transforms: List<LangfuseTransform> = emptyList(),
  private val onCreate: suspend (traceId: Uuid) -> Unit = {},
) : Listener {
  private val batchBuilder: BatchBuilder = BatchBuilder()

  override fun event(event: Event) {
    batchBuilder.event(event)
  }

  override fun flush() {
    CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
      val (traceId, batchIngestion) = batchBuilder.build() ?: return@launch
      val body = transforms.fold(batchIngestion) { acc, transform -> transform(acc) }
      langfuse.client.request {
        method = HttpMethod.Post
        url("api/public/ingestion")
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        setBody(body)
      }
      this@LangfuseListener.onCreate(traceId)
    }
  }

  public companion object {
    public fun setUserId(block: suspend () -> String?): LangfuseTransform =
      transform@{ batchIngestion ->
        val userId = block()
        return@transform transformEvents { ingestionEvent ->
          if (ingestionEvent.type != "trace-create") return@transformEvents ingestionEvent
          ingestionEvent.copy(
            body = ingestionEvent.body +
              mapOf("userId" to userId),
          )
        }.invoke(batchIngestion)
      }

    public fun setSessionId(block: suspend () -> String?): LangfuseTransform =
      transform@{ batchIngestion ->
        val sessionId = block()
        return@transform transformEvents { ingestionEvent ->
          if (ingestionEvent.type != "trace-create") return@transformEvents ingestionEvent
          ingestionEvent.copy(
            body = ingestionEvent.body +
              mapOf("sessionId" to sessionId),
          )
        }.invoke(batchIngestion)
      }

    public fun appendMetadata(block: suspend () -> Map<String, Any>): LangfuseTransform =
      transform@{ batchIngestion ->
        val metadata = block()
        return@transform transformEvents { ingestionEvent ->
          if (ingestionEvent.type != "trace-create") return@transformEvents ingestionEvent
          ingestionEvent.copy(
            body = ingestionEvent.body +
              mapOf("metadata" to ingestionEvent.body["metadata"] as Map<*, *> + metadata),
          )
        }.invoke(batchIngestion)
      }

    public fun transformEvents(
      block: suspend (ingestionEvent: IngestionEvent) -> IngestionEvent,
    ): LangfuseTransform =
      transform@{ batchIngestion ->
        batchIngestion.copy(
          batch = batchIngestion.batch.map { block(it) },
        )
      }
  }
}
