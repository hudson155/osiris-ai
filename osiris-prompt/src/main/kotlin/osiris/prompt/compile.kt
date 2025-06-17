package osiris.prompt

/**
 * Compiles instructions.
 * Replaces instances of {{myVar}} with the value from [buildMap].
 * Note: Variables MUST match "[A-Za-z0-9_]*".
 *
 * Strict mode is used by default,
 * which throws an exception if a variable doesn't have a matching value in [buildMap].
 */
public fun Instructions.compile(buildMap: MutableMap<String, String>.() -> Unit = {}): Instructions =
  compile(strict = true, buildMap)

/**
 * Overload of [compile] for disabling strict mode.
 */
public fun Instructions.compile(
  strict: Boolean,
  buildMap: MutableMap<String, String>.() -> Unit = {},
): Instructions {
  val delegate = this // This looks like 2010s JavaScript.
  return Instructions {
    val instructions = delegate.get()
    val map = buildMap(buildMap)
    val regex = Regex("[{][{] *(?<key>[A-Za-z0-9-_]*) *[}][}]")
    val replacements = regex.findAll(instructions).associate { matchResult ->
      val key = matchResult.groups["key"]!!.value
      return@associate Pair(key, map[key])
    }
    return@Instructions instructions.replace(regex) { match ->
      val key = match.groups["key"]!!.value
      val replacement = replacements[key]
      if (replacement == null) {
        if (strict) error("Unrecognized key: $key.")
        return@replace match.value
      }
      return@replace replacement
    }
  }
}
