package osiris.core

@Suppress("UseDataClass")
public class ModelFactory {
  public val properties: MutableMap<String, Any> = mutableMapOf()
}

public fun modelFactory(block: ModelFactory.() -> Unit = {}): ModelFactory =
  ModelFactory().apply(block)
