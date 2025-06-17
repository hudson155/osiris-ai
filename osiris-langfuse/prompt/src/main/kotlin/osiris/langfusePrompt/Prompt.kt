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

/**
 * Compiles the Langfuse prompt.
 * Replaces instances of {{myVar}} with the value from [variables].
 * Note: Variables MUST match "[A-Za-z0-9_]*".
 *
 * Strict mode is used by default,
 * which throws an exception if a Langfuse prompt variable doesn't have a matching value in [variables].
 */
public fun Prompt.compile(vararg variables: Pair<String, String>): String =
  compile(strict = true, *variables)

/**
 * Overload of [compile] for disabling strict mode.
 */
public fun Prompt.compile(
  strict: Boolean,
  vararg variables: Pair<String, String>,
): String {
  val map = variables.associate { it }
  val regex = Regex("[{][{] *(?<key>[A-Za-z0-9_]*) *[}][}]")
  val replacements = regex.findAll(prompt).associate { matchResult ->
    val key = matchResult.groups["key"]!!.value
    return@associate Pair(key, map[key])
  }
  return prompt.replace(regex) { match ->
    val key = match.groups["key"]!!.value
    val replacement = replacements[key]
    if (replacement == null) {
      if (strict) error("Unrecognized key: $key.")
      return@replace match.value
    }
    return@replace replacement
  }
}
