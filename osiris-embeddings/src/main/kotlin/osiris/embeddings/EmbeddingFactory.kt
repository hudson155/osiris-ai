package osiris.embeddings

import dev.langchain4j.model.embedding.EmbeddingModel

/**
 * The embedding factory is an optional way to instantiate [EmbeddingModel] instances.
 * To use it, add `osiris-open-ai` or another LLM provider.
 */
public open class EmbeddingFactory {
  public val properties: MutableMap<String, Any> = mutableMapOf()
}

public fun embeddingFactory(block: EmbeddingFactory.() -> Unit = {}): EmbeddingFactory =
  EmbeddingFactory().apply(block)
