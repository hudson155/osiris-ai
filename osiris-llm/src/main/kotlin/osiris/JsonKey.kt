package osiris

import io.ktor.util.AttributeKey
import kairo.serialization.KairoJson

private val key: AttributeKey<KairoJson> = AttributeKey("json")

public var Context.json: KairoJson
  get() = attributes.computeIfAbsent(key) { KairoJson() }
  set(value) {
    attributes[key] = value
  }
