package osiris.element.element

import kairo.serialization.KairoJson
import osiris.element.ElementModule

internal abstract class ElementSerializationTest {
  protected val json: KairoJson =
    KairoJson {
      pretty = true
      addModule(ElementModule())
    }
}
