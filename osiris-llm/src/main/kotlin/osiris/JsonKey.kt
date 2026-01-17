package osiris

import io.ktor.util.AttributeKey
import kairo.serialization.KairoJson

private val key: AttributeKey<KairoJson> = AttributeKey("json")

/**
 * This [KairoJson] instance is used for serializing and deserializing JSON when interacting with the LLM.
 */
public var Context.json: KairoJson
  get() = attributes.computeIfAbsent(key) { KairoJson() }
  set(value) {
    attributes[key] = value
  }
