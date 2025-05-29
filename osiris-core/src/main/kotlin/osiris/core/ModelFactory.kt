package osiris.core

public class ModelFactory internal constructor() {
  public val properties: MutableMap<String, Any> = mutableMapOf()
}

public fun modelFactory(block: ModelFactory.() -> Unit = {}): ModelFactory =
  ModelFactory().apply(block)
