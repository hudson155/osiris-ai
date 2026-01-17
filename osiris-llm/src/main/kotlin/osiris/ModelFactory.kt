package osiris

import io.ktor.util.Attributes

/**
 * The [ModelFactory] is used to create and configure LLM models ([Model]).
 * Install osiris-open-ai or use another LLM provider.
 */
public class ModelFactory {
  public val attributes: Attributes = Attributes(concurrent = false)
}

public inline fun modelFactory(block: ModelFactory.() -> Unit): ModelFactory =
  ModelFactory().apply(block)
