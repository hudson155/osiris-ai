package osiris.openAi

import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import kairo.protectedString.ProtectedString
import osiris.core.EmbeddingFactory

private const val openAiApiKeyKey: String = "openAiApiKey"

/**
 * Set the OpenAI key when building the embedding factory.
 */
public var EmbeddingFactory.openAiApiKey: ProtectedString?
  get() = properties[openAiApiKeyKey] as ProtectedString?
  set(value) {
    if (value == null) {
      properties -= openAiApiKeyKey
      return
    }
    properties[openAiApiKeyKey] = value
  }

/**
 * Helper DSL to create a Langchain4j OpenAI embedding model using your embedding factory.
 */
public fun EmbeddingFactory.openAi(
  modelName: String,
  block: OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder.() -> Unit = {},
): OpenAiEmbeddingModel {
  val apiKey = requireNotNull(openAiApiKey) { "OpenAI API key must be set to create a model." }
  return OpenAiEmbeddingModel.builder().apply {
    modelName(modelName)
    @OptIn(ProtectedString.Access::class)
    apiKey(apiKey.value)
    block()
  }.build()
}
