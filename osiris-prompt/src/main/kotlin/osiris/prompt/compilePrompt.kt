package osiris.prompt

/**
 * Compiles a prompt.
 * Replaces instances of {{myVar}} with the value from [buildMap].
 * Note: Variables MUST match "[A-Za-z0-9_]*".
 *
 * Strict mode is used by default,
 * which throws an exception if a Langfuse prompt variable doesn't have a matching value in [buildMap].
 */
public fun compilePrompt(prompt: String, buildMap: MutableMap<String, String>.() -> Unit = {}): String =
  compilePrompt(prompt, strict = true, buildMap)

/**
 * Overload of [compilePrompt] for disabling strict mode.
 */
public fun compilePrompt(
  prompt: String,
  strict: Boolean,
  buildMap: MutableMap<String, String>.() -> Unit = {},
): String {
  val map = buildMap(buildMap)
  val regex = Regex("[{][{] *(?<key>[A-Za-z0-9-_]*) *[}][}]")
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
