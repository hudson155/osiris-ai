package osiris.element.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import osiris.element.element.Element

public fun elementModule(): SerializersModule =
  SerializersModule {
    polymorphic(Element::class) {
    }
  }
