package osiris

import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.anthropic.AnthropicChatModel.AnthropicChatModelBuilder
import io.ktor.util.AttributeKey
import kairo.protectedString.ProtectedString

private val key: AttributeKey<ProtectedString> = AttributeKey("anthropicApiKey")

/**
 * Set the Anthropic API key,
 * which is used by [anthropic].
 */
public var ModelFactory.anthropicApiKey: ProtectedString?
  get() = attributes.getOrNull(key)
  set(value) {
    if (value != null) {
      attributes[key] = value
    } else {
      attributes.remove(key)
    }
  }

/**
 * Instantiates a [Model] for Anthropic,
 * using good defaults.
 */
public fun ModelFactory.anthropic(name: String, block: AnthropicChatModelBuilder.() -> Unit = {}): Model {
  val apiKey = requireNotNull(anthropicApiKey) { "Anthropic API key must be set to create a model." }
  val model = AnthropicChatModel.builder().apply {
    modelName(name)
    @OptIn(ProtectedString.Access::class)
    apiKey(apiKey.value)
    block()
  }.build()
  return Model(model)
}
