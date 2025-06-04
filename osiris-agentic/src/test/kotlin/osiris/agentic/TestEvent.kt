package osiris.agentic

import dev.langchain4j.data.message.AiMessage

internal fun List<Event>.getResponse(): AiMessage {
  val event = filterIsInstance<Event.End>().single()
  return event.execution.messages.last() as AiMessage
}

internal fun List<Event>.getExecution(): Execution {
  val event = filterIsInstance<Event.End>().single()
  return event.execution
}
