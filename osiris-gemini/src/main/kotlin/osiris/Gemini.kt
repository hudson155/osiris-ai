package osiris

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder
import io.ktor.util.AttributeKey
import kairo.protectedString.ProtectedString

private val key: AttributeKey<ProtectedString> = AttributeKey("geminiApiKey")

/**
 * Set the Gemini API key,
 * which is used by [gemini].
 */
public var ModelFactory.geminiApiKey: ProtectedString?
  get() = attributes.getOrNull(key)
  set(value) {
    if (value != null) {
      attributes[key] = value
    } else {
      attributes.remove(key)
    }
  }

/**
 * Instantiates a [Model] for Gemini,
 * using good defaults.
 */
public fun ModelFactory.gemini(name: String, block: GoogleAiGeminiChatModelBuilder.() -> Unit = {}): Model {
  val apiKey = requireNotNull(geminiApiKey) { "Gemini API key must be set to create a model." }
  val model = GoogleAiGeminiChatModel.builder().apply {
    modelName(name)
    @OptIn(ProtectedString.Access::class)
    apiKey(apiKey.value)
    block()
  }.build()
  return Model(model)
}
