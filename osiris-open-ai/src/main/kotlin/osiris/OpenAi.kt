package osiris

import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel.OpenAiChatModelBuilder
import io.ktor.util.AttributeKey
import kairo.protectedString.ProtectedString

private val key: AttributeKey<ProtectedString> = AttributeKey("openAiApiKey")

/**
 * Set the OpenAI API key,
 * which is used by [openAi].
 */
public var ModelFactory.openAiApiKey: ProtectedString?
  get() = attributes.getOrNull(key)
  set(value) {
    if (value != null) {
      attributes[key] = value
    } else {
      attributes.remove(key)
    }
  }

/**
 * Instantiates a [Model] for OpenAI,
 * using good defaults.
 */
public fun ModelFactory.openAi(name: String, block: OpenAiChatModelBuilder.() -> Unit = {}): Model {
  val apiKey = requireNotNull(openAiApiKey) { "OpenAI API key must be set to create a model." }
  val model = OpenAiChatModel.builder().apply {
    modelName(name)
    @OptIn(ProtectedString.Access::class)
    apiKey(apiKey.value)
    strictJsonSchema(true)
    strictTools(true)
    block()
  }.build()
  return Model(model)
}
