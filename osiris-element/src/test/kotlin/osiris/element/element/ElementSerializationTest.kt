package osiris.element.element

import kairo.serialization.prettyPrint
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import osiris.element.serialization.elementModule

internal abstract class ElementSerializationTest {
  protected val json: Json = Json {
    prettyPrint()
    serializersModule += elementModule()
  }
}
