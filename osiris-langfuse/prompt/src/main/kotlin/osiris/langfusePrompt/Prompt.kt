package osiris.langfusePrompt

import kotlin.uuid.Uuid

public data class Prompt(
  val id: Uuid,
  val name: String,
  val prompt: String,
)
