package osiris.langfusePrompt

import kotlin.uuid.Uuid

public data class Prompt(
  val id: Uuid,
  val name: String,
  val prompt: String,
)

public fun Prompt.compile(vararg variables: Pair<String, String>): String =
  compile(strict = true, *variables)

public fun Prompt.compile(
  strict: Boolean,
  vararg variables: Pair<String, String>,
): String {
  val map = variables.associate { it }
  val regex = Regex("[{][{](?<key>[a-z][A-Za-z0-9]*)[}][}]")
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
