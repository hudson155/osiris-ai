package osiris.langfusePrompt

import kotlin.uuid.Uuid

/**
 * A Langfuse prompt.
 */
public data class Prompt(
  val id: Uuid,
  val name: String,
  val prompt: String,
)
