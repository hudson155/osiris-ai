package osiris.langfusePrompt

import kotlin.uuid.Uuid
import osiris.prompt.compilePrompt

/**
 * A Langfuse prompt.
 */
public data class Prompt(
  val id: Uuid,
  val name: String,
  val prompt: String,
)

public fun Prompt.compile(buildMap: MutableMap<String, String>.() -> Unit = {}): String =
  compilePrompt(prompt, buildMap)

public fun Prompt.compile(
  strict: Boolean,
  buildMap: MutableMap<String, String>.() -> Unit = {},
): String =
  compilePrompt(prompt, strict, buildMap)
