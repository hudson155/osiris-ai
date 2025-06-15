package osiris.openAi

import dev.langchain4j.model.openai.OpenAiChatModel
import kairo.protectedString.ProtectedString
import osiris.core.ModelFactory

private const val openAiApiKeyKey: String = "openAiApiKey"

public var ModelFactory.openAiApiKey: ProtectedString?
  get() = properties[openAiApiKeyKey] as ProtectedString?
  set(value) {
    if (value == null) {
      properties -= openAiApiKeyKey
      return
    }
    properties[openAiApiKeyKey] = value
  }

public fun ModelFactory.openAi(
  modelName: String,
  block: OpenAiChatModel.OpenAiChatModelBuilder.() -> Unit = {},
): OpenAiChatModel {
  val apiKey = requireNotNull(openAiApiKey) { "OpenAI API key must be set to create a model." }
  return OpenAiChatModel.builder().apply {
    modelName(modelName)
    @OptIn(ProtectedString.Access::class)
    apiKey(apiKey.value)
    strictJsonSchema(true)
    strictTools(true)
    block()
  }.build()
}
