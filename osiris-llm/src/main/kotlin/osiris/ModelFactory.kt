package osiris

import io.ktor.util.Attributes

public class ModelFactory {
  public val attributes: Attributes = Attributes(concurrent = false)
}

public fun modelFactory(block: ModelFactory.() -> Unit): ModelFactory =
  ModelFactory().apply(block)
