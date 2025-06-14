package osiris.core2

import dev.langchain4j.model.chat.ChatModel

/**
 * The model factory is an optional way to instantiate [ChatModel] instances.
 * To use it, add `osiris-open-ai` or another LLM provider.
 */
public open class ModelFactory {
  public val properties: MutableMap<String, Any> = mutableMapOf()
}

public fun modelFactory(block: ModelFactory.() -> Unit = {}): ModelFactory =
  ModelFactory().apply(block)
