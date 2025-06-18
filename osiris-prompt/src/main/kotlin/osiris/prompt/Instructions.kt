package osiris.prompt

import com.google.common.io.Resources

/**
 * Agent instructions can be provided asynchronously,
 * so that they can be fetched from external sources.
 */
public fun interface Instructions {
  public suspend fun get(): String

  public companion object
}

public fun Instructions.Companion.fromResource(resourceName: String): Instructions =
  Instructions { Resources.getResource(resourceName).readText().trim() }
