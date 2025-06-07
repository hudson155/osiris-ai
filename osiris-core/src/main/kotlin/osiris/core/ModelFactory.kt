package osiris.core

public open class ModelFactory {
  public val properties: MutableMap<String, Any> = mutableMapOf()
}

public fun modelFactory(block: ModelFactory.() -> Unit = {}): ModelFactory =
  ModelFactory().apply(block)
